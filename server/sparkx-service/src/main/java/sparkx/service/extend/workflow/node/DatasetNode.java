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
import sparkx.common.exception.BusinessException;
import sparkx.common.utils.Tool;
import sparkx.service.entity.workflow.ApplicationWorkflowRuntimeContextEntity;
import sparkx.service.extend.workflow.IWorkflowNode;
import sparkx.service.helper.ApplicationHelper;
import sparkx.service.mapper.application.ApplicationWorkflowRuntimeContextMapper;
import sparkx.service.service.interfaces.dataset.IDatasetSearchService;
import sparkx.service.vo.dataset.DatasetSearchVo;
import sparkx.service.vo.dataset.SearchVo;
import sparkx.service.vo.workflow.EdgeVo;
import sparkx.service.vo.workflow.NextAnswerNodeVo;
import sparkx.service.vo.workflow.NodeRuntimeVo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class DatasetNode implements IWorkflowNode {

    @Setter
    public SseEmitter emitter;

    @Setter
    public CountDownLatch latch;

    @Autowired
    ApplicationWorkflowRuntimeContextMapper applicationWorkflowRuntimeContextMapper;

    @Autowired
    ApplicationHelper applicationHelper;

    @Autowired
    IDatasetSearchService searchService;

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

        // 上个节点的信息
        ApplicationWorkflowRuntimeContextEntity context =
                applicationHelper.getRuntimeContext(runtimeVo.getRuntimeId(), runtimeVo.getSourceId(), inputSourceId);
        if (context == null) {
            return null;
        }

        String question;
        JSONObject preOutput = JSONUtil.parseObj(context.getOutputData());
        if (!inputArr.isEmpty()) {
            String inputData = inputArr.get(1).toString();
            question = preOutput.get(inputData).toString();
        } else {
            question = "";
        }

        JSONArray datasetsArr = nodeObject.getJSONArray("datasets");
        List<String> datasetIds = new ArrayList<>();
        for (int i = 0; i < datasetsArr.size(); i++) {
            datasetIds.add(datasetsArr.getJSONObject(i).getStr("datasetId"));
        }

        // 记录运行时数据
        ApplicationWorkflowRuntimeContextEntity contextEntity = new ApplicationWorkflowRuntimeContextEntity();
        contextEntity.setStep(context.getStep() + 1);
        contextEntity.setNodeType(NodeTypeEnum.DATASET.getCode());
        contextEntity.setRuntimeId(runtimeVo.getRuntimeId());

        // 记录问题分类节点的输出
        preOutput.set("node.question", question);
        preOutput.set("node.datasets", String.join(",", datasetIds));
        contextEntity.setOutputData(preOutput.toString());

        contextEntity.setModelData(nodeObject.toString()); // 记录节点配置信息
        contextEntity.setCell(runtimeVo.getNodeInfo().getId());
        contextEntity.setCreateTime(Tool.nowDateTime());
        applicationWorkflowRuntimeContextMapper.insert(contextEntity);

        // 本节点输出内容
        String answer = datasetAnswer(question, String.join(",", datasetIds), contextEntity);

        // 查看下一个节点是否是回复节点，且回复的内容是本节点的输出
        NextAnswerNodeVo nextAnswerNodeInfo = applicationHelper.checkNextIsAnswerNode(runtimeVo);
        if (nextAnswerNodeInfo.isNodeIsAnswer() && nextAnswerNodeInfo.getAnswerType() == 1) {
            try {

                emitter.send(Tool.buildSendData(contextEntity.getRuntimeId(), contextEntity.getCell(), answer));
            } catch (Exception e) {
                log.error("知识库检索节点错误, {}", e.getMessage());
                throw new BusinessException("知识库检索节点错误");
            }
        }

        latch.countDown();

        // 获取下一个节点
        return runtimeVo.getEdges().get(runtimeVo.getNodeInfo().getId());
    }

    /**
     * 知识库检索
     * @param question String
     * @param datasetIds String
     * @param context ApplicationWorkflowRuntimeContextEntity
     * @return String
     */
    private String datasetAnswer(String question, String datasetIds, ApplicationWorkflowRuntimeContextEntity context) {

        JSONObject nodeObject = JSONUtil.parseObj(context.getModelData());

        DatasetSearchVo searchDataVo = new DatasetSearchVo();
        searchDataVo.setKeyword(question);
        searchDataVo.setDatasetIds(datasetIds);
        searchDataVo.setSimilarity(nodeObject.getDouble("similarity"));
        searchDataVo.setTopRank(nodeObject.getInt("topRank"));
        searchDataVo.setType("embedding");
        List<SearchVo> searchRes = searchService.search(searchDataVo);

        // 写入上下文，记录召回信息
        JSONObject outputData = JSONUtil.parseObj(context.getOutputData());
        outputData.set("datasets.search", JSONUtil.toJsonStr(searchRes));
        outputData.set("datasets.rerankModelId", nodeObject.getStr("rerankModelId"));
        outputData.set("datasets.originalResult", searchRes.stream().map(SearchVo::getContent).collect(Collectors.toList()));
        outputData.set("sys.result", searchRes.stream().map(SearchVo::getContent).collect(Collectors.joining("\n")));
        context.setOutputData(outputData.toString());
        applicationWorkflowRuntimeContextMapper.updateById(context);

        return searchRes.stream().map(SearchVo::getContent).collect(Collectors.joining());
    }
}