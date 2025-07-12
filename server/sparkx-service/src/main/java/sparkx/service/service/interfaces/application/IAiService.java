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

import dev.langchain4j.service.*;

import java.util.List;

public interface IAiService {

    /**
     * 不带角色设定的流式输出
     * @param userMessage String
     * @return TokenStream
     */
    TokenStream chatInTokenStream(String userMessage);

    /**
     * 带角色设定的流式输出
     * @param systemMessage String
     * @param userMessage String
     * @return TokenStream
     */
    @SystemMessage("{{message}}")
    TokenStream chatWithSystem(@V("message") String systemMessage, @UserMessage String userMessage);

    Result<List<String>> chat(String userMessage);
}