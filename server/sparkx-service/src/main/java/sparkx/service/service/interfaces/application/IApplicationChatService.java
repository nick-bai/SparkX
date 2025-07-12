// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.service.service.interfaces.application;

import sparkx.service.vo.application.*;

import java.util.List;

public interface IApplicationChatService {

    /**
     * 获取应用信息
     * @param chatInfoVo ChatInfoVo
     * @return ApplicationChatVo
     */
    ApplicationSimpleVo getChatInfo(ChatInfoVo chatInfoVo);

    /**
     * 获取会话记录
     * @param chatInfoVo ChatInfoVo
     * @return List<ApplicationSimpleSessionVo>
     */
    List<ApplicationSimpleSessionVo> getChatSesstionList(ChatInfoVo chatInfoVo);

    /**
     * 创建会话
     * @param sessionVo SessionVo
     * @return String
     */
    String createSession(SessionVo sessionVo);

    /**
     * 更新会话
     * @param sessionVo SessionVo
     */
    void updateSession(SessionVo sessionVo);

    /**
     * 记录对话日志
     * @param logVo ApplicationLogVo
     * @return Integer
     */
    Integer writeLog(ApplicationLogVo logVo);

    /**
     * 评价回答
     * @param appraiseVo AppraiseVo
     */
    void appraise(AppraiseVo appraiseVo);

    /**
     * 删除会话
     * @param sessionId String
     */
    void delSession(String sessionId);

    /**
     * 获取聊天记录
     * @param sessionId String
     * @return List<ApplicationLogVo>
     */
    List<ApplicationLogVo> getChatLog(String sessionId);
}
