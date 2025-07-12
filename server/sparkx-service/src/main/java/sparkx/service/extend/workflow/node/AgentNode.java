// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.service.extend.workflow.node;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import dev.langchain4j.service.TokenStream;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import sparkx.common.enums.NodeTypeEnum;
import sparkx.common.exception.BusinessException;
import sparkx.common.utils.Tool;
import sparkx.service.entity.application.ApplicationEntity;
import sparkx.service.entity.workflow.ApplicationWorkflowRuntimeContextEntity;
import sparkx.service.extend.chat.AgentChat;
import sparkx.service.extend.workflow.IWorkflowNode;
import sparkx.service.helper.ApplicationHelper;
import sparkx.service.helper.SseEmitterHelper;
import sparkx.service.mapper.application.ApplicationMapper;
import sparkx.service.mapper.application.ApplicationWorkflowRuntimeContextMapper;
import sparkx.service.validate.application.ApplicationChatValidate;
import sparkx.service.vo.workflow.EdgeVo;
import sparkx.service.vo.workflow.NodeRuntimeVo;

import java.util.List;
import java.util.concurrent.CountDownLatch;

@Component
@Slf4j
public class AgentNode implements IWorkflowNode {

    @Setter
    public SseEmitter emitter;

    @Setter
    public CountDownLatch latch;

    @Autowired
    ApplicationWorkflowRuntimeContextMapper applicationWorkflowRuntimeContextMapper;

    @Autowired
    ApplicationMapper applicationMapper;

    @Autowired
    AgentChat agentChat;

    @Autowired
    SseEmitterHelper sseEmitterHelper;

    @Autowired
    ApplicationHelper applicationHelper;

    @Override
    public List<EdgeVo> handle(NodeRuntimeVo runtimeVo) {

        JSONObject nodeObject = runtimeVo.getNodeInfo().getData();
        // 本节点输入的参数
        JSONArray inputArr = nodeObject.getJSONArray("inputData");
        String inputSourceId;
        if (!inputArr.isEmpty()) {
            inputSourceId = inputArr.get(0).toString();
        } else {
            inputSourceId = "";
        }

        // 获取上一个节点的信息
        ApplicationWorkflowRuntimeContextEntity context =
                applicationHelper.getRuntimeContext(runtimeVo.getRuntimeId(), runtimeVo.getSourceId(), inputSourceId);
        if (context == null) {
            return null;
        }

        String inputData = inputArr.get(1).toString();
        JSONObject preOutput = JSONUtil.parseObj(context.getOutputData());

        String question = preOutput.get(inputData).toString();
        String agentId = nodeObject.getStr("agentId");

        // 获取应用信息
        ApplicationEntity applicationInfo = applicationMapper.selectById(agentId);
        if (applicationInfo == null) {
            throw new BusinessException("应用配置错误");
        }

        // 记录运行时数据
        ApplicationWorkflowRuntimeContextEntity contextEntity = new ApplicationWorkflowRuntimeContextEntity();
        contextEntity.setStep(context.getStep() + 1);
        contextEntity.setNodeType(NodeTypeEnum.AGENT.getCode());
        contextEntity.setRuntimeId(runtimeVo.getRuntimeId());
        contextEntity.setOutputData(preOutput.toString());
        contextEntity.setCell(runtimeVo.getNodeInfo().getId());
        contextEntity.setCreateTime(Tool.nowDateTime());
        applicationWorkflowRuntimeContextMapper.insert(contextEntity);

        ApplicationChatValidate validate = new ApplicationChatValidate();
        validate.setContent(question);
        validate.setAppId(agentId);
        validate.setDatasetList(applicationHelper.getRelationDatasetList(agentId));
        validate.setContextId(contextEntity.getId());
        validate.setCell(contextEntity.getCell()); // 以次区分不同的节点的上下文聊天记录
        validate.setSessionId(runtimeVo.getSessionId());

        applicationInfo.setUserId(runtimeVo.getUserId()); // 设置为运行用户

        try {

            TokenStream tokenStream = agentChat.streamChat(applicationInfo, validate);

            sseEmitterHelper.asyncSend2Client(tokenStream, emitter, runtimeVo.getRuntimeId(),
                    runtimeVo.getNodeInfo().getId(), true, (response) -> {

                // 更新运行时数据
                ApplicationWorkflowRuntimeContextEntity contextEntityUpdate =
                        applicationWorkflowRuntimeContextMapper.selectById(contextEntity.getId());

                // 模型使用情况
                JSONObject modelData = JSONUtil.createObj();
                JSONObject llmResData = JSONUtil.parseObj(response);
                modelData.set("inputTokenCount", llmResData.getStr("inputTokenCount"));
                modelData.set("outputTokenCount", llmResData.getStr("outputTokenCount"));
                modelData.set("totalTokenCount", llmResData.getStr("totalTokenCount"));
                contextEntityUpdate.setModelData(modelData.toString());

                // 记录问题分类节点的输出
                String answer = llmResData.getStr("content");
                JSONObject outputData = JSONUtil.parseObj(contextEntityUpdate.getOutputData());
                outputData.set("agent.input", question);
                outputData.set("sys.agentContent", answer);

                contextEntityUpdate.setOutputData(outputData.toString());
                applicationWorkflowRuntimeContextMapper.updateById(contextEntityUpdate);

                latch.countDown();
            });

        } catch (Exception e) {
            log.error("回复节点构建llm错误：", e);
            return null;
        }

        // 获取下一个节点
        return runtimeVo.getEdges().get(runtimeVo.getNodeInfo().getId());
    }
}