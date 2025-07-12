// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.service.vo.workflow;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import lombok.Data;
import sparkx.service.entity.application.ApplicationEntity;
import sparkx.service.service.interfaces.application.IAiService;
import sparkx.service.validate.application.ApplicationChatValidate;

import java.io.Serial;
import java.io.Serializable;

@Data
public class LlmAnswerVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 应用信息
     */
    private ApplicationEntity application;

    /**
     * 应用验证信息
     */
    private ApplicationChatValidate validate;

    /**
     * 流式输出模型
     */
    private StreamingChatModel streamingChatModel;

    /**
     * 阻塞输出模型
     */
    private ChatModel chatLanguageModel;

    /**
     * AI服务对象
     */
    private IAiService assistant;

    /**
     * 用户输入信息
     */
    private String userMessage;
}