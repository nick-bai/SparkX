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
import org.springframework.web.bind.annotation.*;
import sparkx.common.core.AjaxResult;
import sparkx.service.service.interfaces.application.IApplicationChatService;
import sparkx.service.vo.application.*;

import java.util.List;

@RequestMapping("/api/chat")
@RestController
public class ChatController {

    @Autowired
    IApplicationChatService iApplicationChatService;

    /**
     * 应用聊天详情
     */
    @GetMapping("/info")
    public AjaxResult<ApplicationSimpleVo> info(ChatInfoVo chatInfoVo) {

        return AjaxResult.success(iApplicationChatService.getChatInfo(chatInfoVo));
    }

    /**
     * 获取会话列表
     */
    @GetMapping("/sessionList")
    public AjaxResult<List<ApplicationSimpleSessionVo>> sessionList(ChatInfoVo chatInfoVo) {

        return AjaxResult.success(iApplicationChatService.getChatSesstionList(chatInfoVo));
    }

    /**
     * 创建会话
     */
    @PostMapping("/createSession")
    public AjaxResult<Object> createSession(@RequestBody SessionVo sessionVo) {

        return AjaxResult.success(iApplicationChatService.createSession(sessionVo));
    }

    /**
     * 更新会话
     */
    @PostMapping("/updateSession")
    public AjaxResult<Object> updateSession(@RequestBody SessionVo sessionVo) {

        iApplicationChatService.updateSession(sessionVo);
        return AjaxResult.success();
    }

    /**
     * 记录对话日志
     */
    @PostMapping("/writeLog")
    public AjaxResult<Object> writeLog(@RequestBody ApplicationLogVo logVo) {

        return AjaxResult.success(iApplicationChatService.writeLog(logVo));
    }

    /**
     * 评价回答
     */
    @PostMapping("/appraise")
    public AjaxResult<Object> appraise(@RequestBody AppraiseVo appraiseVo) {

        iApplicationChatService.appraise(appraiseVo);
        return AjaxResult.success();
    }

    /**
     * 删除会话
     */
    @GetMapping("/delSession")
    public AjaxResult<Object> delSession(@RequestParam("sessionId") String sessionId) {

        iApplicationChatService.delSession(sessionId);
        return AjaxResult.success();
    }

    /**
     * 获取聊天记录
     */
    @GetMapping("/chatLog")
    public AjaxResult<List<ApplicationLogVo>> chatLog(@RequestParam("sessionId") String sessionId) {

        return AjaxResult.success(iApplicationChatService.getChatLog(sessionId));
    }
}