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

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import sparkx.common.enums.NodeTypeEnum;
import sparkx.common.utils.Tool;
import sparkx.service.entity.workflow.ApplicationWorkflowRuntimeContextEntity;
import sparkx.service.extend.workflow.IWorkflowNode;
import sparkx.service.helper.ApplicationHelper;
import sparkx.service.mapper.application.ApplicationWorkflowRuntimeContextMapper;
import sparkx.service.vo.workflow.EdgeVo;
import sparkx.service.vo.workflow.NodeRuntimeVo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Component
public class SwitchNode implements IWorkflowNode {

    @Setter
    public SseEmitter emitter;

    @Setter
    public CountDownLatch latch;

    @Autowired
    ApplicationWorkflowRuntimeContextMapper applicationWorkflowRuntimeContextMapper;

    @Autowired
    ApplicationHelper applicationHelper;

    @Override
    public List<EdgeVo> handle(NodeRuntimeVo runtimeVo) {

        JSONObject nodeObject = runtimeVo.getNodeInfo().getData();
        // 分支配置
        JSONArray ifBranch = nodeObject.getJSONArray("ifBranch");

        boolean match = false;
        JSONObject preOutput = null;
        ApplicationWorkflowRuntimeContextEntity context = null;
        int index = -1;

        for (int i = 0; i < ifBranch.size(); i++) {

            JSONObject nowNodeObject = ifBranch.getJSONObject(i);
            Integer judging = nowNodeObject.getInt("switch");
            // 本节点输入的参数
            JSONArray inputArr = nowNodeObject.getJSONArray("data");
            List<Boolean> matchArr = new ArrayList<>();

            for (int j = 0; j < inputArr.size(); j++) {

                JSONArray inputJsonArr = inputArr.getJSONObject(j).getJSONArray("input");
                String inputIndex = inputJsonArr.get(1).toString();
                String inputSourceId = inputJsonArr.get(0).toString();
                // 获取上一个节点的信息
                context = applicationHelper.getRuntimeContext(runtimeVo.getRuntimeId(), runtimeVo.getSourceId(), inputSourceId);
                if (context == null) {
                    return null;
                }

                preOutput = JSONUtil.parseObj(context.getOutputData());
                String inputData = preOutput.get(inputIndex).toString();
                int tips = inputArr.getJSONObject(j).getInt("tips");
                String value = inputArr.getJSONObject(j).getStr("value");

                Boolean matchRes = switchTest(tips, inputData, value);
                if (matchRes) {
                    preOutput.set("switch.result", nowNodeObject.getStr("type"));
                }
                matchArr.add(matchRes);
            }

            // AND 条件
            if (judging.equals(1)) {

                boolean hasMatch = matchArr.stream().allMatch(item -> item);
                if (hasMatch) {
                    match = true;
                    index = i;
                }
            } else { // OR 条件

                for (Boolean item : matchArr) {
                    if (item) {
                        match = true;
                        index = i;
                        break;
                    }
                }
            }

            if (match) {
                break;
            }
        }

        // 获取下一个节点
        List<EdgeVo> nextEdgeVoList = runtimeVo.getEdges().get(runtimeVo.getNodeInfo().getId());
        // 如果if分支未命中，则选取else分支
        if (!match) {
            if (preOutput != null) {
                preOutput.set("switch.result", "else");
            }
            index = nextEdgeVoList.size() - 1;
        }

        // 记录运行时数据
        if (context != null) {
            ApplicationWorkflowRuntimeContextEntity contextEntity = new ApplicationWorkflowRuntimeContextEntity();
            contextEntity.setStep(context.getStep() + 1);
            contextEntity.setNodeType(NodeTypeEnum.SWITCH.getCode());
            contextEntity.setRuntimeId(runtimeVo.getRuntimeId());
            // 记录问题分类节点的输入
            contextEntity.setOutputData(preOutput.toString());
            contextEntity.setCell(runtimeVo.getNodeInfo().getId());
            contextEntity.setCreateTime(Tool.nowDateTime());
            applicationWorkflowRuntimeContextMapper.insert(contextEntity);
        }

        List<EdgeVo> newEdgeVoList = new LinkedList<>();
        newEdgeVoList.add(nextEdgeVoList.get(index));

        latch.countDown();

        return newEdgeVoList;
    }

    /**
     * 条件判断
     * @param tips int
     * @param inputVal String
     * @param value String
     * @return boolean
     */
    private boolean switchTest(int tips, String inputVal, String value) {
        boolean match = false;
        switch (tips) {
            // 为空
            case 1 -> {
                if (StrUtil.isBlank(inputVal)) {
                    match = true;
                }
            }
            // 不为空
            case 2 -> {
                if (StrUtil.isNotBlank(inputVal)) {
                    match = true;
                }
            }
            // 包含
            case 3 -> {
                if (inputVal.contains(value)) {
                    match = true;
                }
            }
            // 不包含
            case 4 -> {
                if (!inputVal.contains(value)) {
                    match = true;
                }
            }
            // 等于
            case 5 -> {
                if (inputVal.equals(value)) {
                    match = true;
                }
            }
            // 大于等于
            case 6 -> {
                if (Integer.parseInt(inputVal) >= Integer.parseInt(value)) {
                    match = true;
                }
            }
            // 小于
            case 7 -> {
                if (Integer.parseInt(inputVal) < Integer.parseInt(value)) {
                    match = true;
                }
            }
            // 长度等于
            case 8 -> {
                if (inputVal.length() == Integer.parseInt(value)) {
                    match = true;
                }
            }
            // 长度大于等于
            case 9 -> {
                if (inputVal.length() >= Integer.parseInt(value)) {
                    match = true;
                }
            }
            // 长度大于
            case 10 -> {
                if (inputVal.length() > Integer.parseInt(value)) {
                    match = true;
                }
            }
            // 长度小于等于
            case 11 -> {
                if (inputVal.length() <= Integer.parseInt(value)) {
                    match = true;
                }
            }
            // 长度小于
            case 12 -> {
                if (inputVal.length() < Integer.parseInt(value)) {
                    match = true;
                }
            }
        }

        return match;
    }
}