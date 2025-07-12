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
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import sparkx.common.core.AjaxResult;
import sparkx.common.core.PageResult;
import sparkx.service.service.interfaces.application.IApplicationService;
import sparkx.service.validate.application.ApplicationAddValidate;
import sparkx.service.validate.application.ApplicationChatValidate;
import sparkx.service.validate.application.ApplicationSaveValidate;
import sparkx.service.vo.application.*;

@RequestMapping("/api/application")
@RestController
public class ApplicationController {

    @Autowired
    IApplicationService iApplicationService;

    /**
     * 应用列表
     */
    @GetMapping("/list")
    public AjaxResult<PageResult<ApplicationListVo>> list(ApplicationQueryVo queryVo) {

        return AjaxResult.success(iApplicationService.getApplicationList(queryVo));
    }

    /**
     * 添加应用
     */
    @PostMapping("/add")
    public AjaxResult<Object> add(@RequestBody @Validated ApplicationAddValidate validate) {

        return AjaxResult.success(iApplicationService.addApplication(validate));
    }

    /**
     * 获取应用详情
     */
    @GetMapping("/detail")
    public AjaxResult<ApplicationVo> applicationInfo(@RequestParam("appId") String appId) {

        return AjaxResult.success(iApplicationService.getApplicationInfo(appId));
    }

    /**
     * 根据accessToken获取详情
     */
    @GetMapping("/chatDetail")
    public AjaxResult<ApplicationVo> chatDetail(@RequestParam("accessToken") String accessToken) {

        return AjaxResult.success(iApplicationService.getApplicationInfoByToken(accessToken));
    }

    /**
     * 保存应用设置
     */
    @PostMapping("/save")
    public AjaxResult<Object> save(@RequestBody @Validated ApplicationSaveValidate validate) {

        iApplicationService.saveApplication(validate);
        return AjaxResult.success();
    }

    /**
     * 应用聊天
     */
    @PostMapping(value = "/sseChat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter sseChat(@RequestBody ApplicationChatValidate validate) {

        return iApplicationService.sseChat(validate);
    }

    /**
     * 统计数据
     */
    @GetMapping("/census")
    public AjaxResult<CensusVo> census(@RequestParam("days") Integer days, @RequestParam("startTime") String startTime,
                                       @RequestParam("endTime") String endTime) {

        return AjaxResult.success(iApplicationService.getCensusData(days, startTime, endTime));
    }

    /**
     * 对话记录
     */
    @GetMapping("/log")
    public AjaxResult<PageResult<SessionListVo>> log(SessionQueryVo queryVo) {

        return AjaxResult.success(iApplicationService.getSessionLog(queryVo));
    }

    /**
     * 删除应用
     */
    @GetMapping("/del")
    public AjaxResult<Object> del(@RequestParam("appId") String appId) {

        iApplicationService.deleteApp(appId);
        return AjaxResult.success();
    }
}