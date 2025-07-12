// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.service.service.interfaces.workflow;

import sparkx.service.validate.workflow.SaveWorkflowValidate;
import sparkx.service.vo.workflow.RuntimeContextVo;
import sparkx.service.vo.workflow.SaveWorkflowVo;

import java.util.List;

public interface IWorkflowService {

    /**
     * 获取流程数据
     * @param appId String
     * @return SaveWorkflowVo
     */
    SaveWorkflowVo getFlowInfo(String appId);

    /**
     * 保存流程设计
     * @param validate SaveWorkflowValidate
     */
    void saveWorkflow(SaveWorkflowValidate validate);

    /**
     * 获取执行详情
     * @param runtimeId long
     * @return List<RuntimeContextVo>
     */
    List<RuntimeContextVo> getRuntimeDetail(long runtimeId);
}