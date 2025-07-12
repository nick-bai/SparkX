// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.web.controller.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sparkx.common.core.AjaxResult;
import sparkx.service.service.interfaces.workflow.IWorkflowService;
import sparkx.service.validate.workflow.SaveWorkflowValidate;
import sparkx.service.vo.workflow.RuntimeContextVo;
import sparkx.service.vo.workflow.SaveWorkflowVo;

import java.util.List;

@RequestMapping("/api/workflow")
@RestController
public class WorkflowController {

    @Autowired
    IWorkflowService iWorkflowService;

    /**
     * 获取流程数据
     * @param appId String
     */
    @GetMapping("/info")
    public AjaxResult<SaveWorkflowVo> info(String appId) {

        return AjaxResult.success(iWorkflowService.getFlowInfo(appId));
    }

    /**
     * 保存流程设计
     */
    @PostMapping("/save")
    public AjaxResult<Object> save(@RequestBody @Validated SaveWorkflowValidate validate) {

        iWorkflowService.saveWorkflow(validate);
        return AjaxResult.success();
    }

    /**
     * 查看执行详情
     */
    @GetMapping("/runDetail")
    public AjaxResult<List<RuntimeContextVo>> runDetail(@RequestParam("runtimeId") long runtimeId) {

        return AjaxResult.success(iWorkflowService.getRuntimeDetail(runtimeId));
    }
}