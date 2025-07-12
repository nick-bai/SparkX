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
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import sparkx.common.enums.NodeTypeEnum;
import sparkx.common.utils.Tool;
import sparkx.service.entity.workflow.ApplicationWorkflowRuntimeContextEntity;
import sparkx.service.extend.workflow.IWorkflowNode;
import sparkx.service.helper.ApplicationHelper;
import sparkx.service.helper.SseEmitterHelper;
import sparkx.service.mapper.application.ApplicationWorkflowRuntimeContextMapper;
import sparkx.service.vo.workflow.EdgeVo;
import sparkx.service.vo.workflow.NodeRuntimeVo;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Component
@Slf4j
public class AnswerNode implements IWorkflowNode {

    @Autowired
    SseEmitterHelper sseEmitterHelper;

    @Setter
    public CountDownLatch latch;

    @Autowired
    ApplicationWorkflowRuntimeContextMapper applicationWorkflowRuntimeContextMapper;

    @Setter
    public SseEmitter emitter;

    @Autowired
    ApplicationHelper applicationHelper;

    @Override
    public List<EdgeVo> handle(NodeRuntimeVo runtimeVo) {

        try {

            JSONObject nodeObject = runtimeVo.getNodeInfo().getData();
            Integer answerType = nodeObject.getInt("answerType");
            // 本节点输入的参数
            JSONArray inputArr = nodeObject.getJSONArray("inputData");
            String inputSourceId;
            if (!inputArr.isEmpty()) {
                inputSourceId = inputArr.get(0).toString();
            } else {
                inputSourceId = "";
            }

            // 上个节点的信息
            ApplicationWorkflowRuntimeContextEntity context =
                    applicationHelper.getRuntimeContext(runtimeVo.getRuntimeId(), runtimeVo.getSourceId(), inputSourceId);
            if (context == null) {
                return null;
            }

            // 记录运行时数据
            ApplicationWorkflowRuntimeContextEntity contextEntity = new ApplicationWorkflowRuntimeContextEntity();
            contextEntity.setStep(context.getStep() + 1);
            contextEntity.setNodeType(NodeTypeEnum.ANSWER.getCode());
            contextEntity.setRuntimeId(runtimeVo.getRuntimeId());

            JSONObject preOutput = JSONUtil.parseObj(context.getOutputData());
            if (answerType.equals(1)) {

                // 找出回复内容
                String returnAnswerType = inputArr.get(1).toString();
                String answer = preOutput.get(returnAnswerType).toString();

                String outputNode = inputArr.get(0).toString();
                // 回复节点设置的不是上一个节点的输出，则在此输出
                if (!outputNode.equals(context.getCell())) {
                    emitter.send(Tool.buildSendData(context.getRuntimeId(), context.getCell(), answer));
                }

                // 记录问题分类节点的输出
                preOutput.set("sys.answer", answer);
            } else {
                emitter.send(Tool.buildSendData(context.getRuntimeId(), context.getCell(), nodeObject.getStr("answer")));

                // 记录问题分类节点的输出
                preOutput.set("sys.answer", nodeObject.getStr("answer"));
            }

            contextEntity.setOutputData(preOutput.toString());
            contextEntity.setCell(runtimeVo.getNodeInfo().getId());
            contextEntity.setCreateTime(Tool.nowDateTime());
            applicationWorkflowRuntimeContextMapper.insert(contextEntity);

        } catch (IOException e) {
            log.error("回复节点发生错误, {}", e.getMessage());
            sseEmitterHelper.sendErrorSse(emitter, e.getMessage());
        }

        latch.countDown();

        // 获取下一个节点
        return runtimeVo.getEdges().get(runtimeVo.getNodeInfo().getId());
    }
}