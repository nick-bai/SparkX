// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.service.vo.application;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class SseChatVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 聊天内容
     */
    private String content;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 应用ID
     */
    private String appId;

    /**
     * 是否重新回答
     */
    private boolean reTry;
}