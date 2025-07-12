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
public class ApplicationSimpleSessionVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 会话id
     */
    private String sessionId;

    /**
     * 应用id
     */
    private String appId;

    /**
     * 会话标题
     */
    private String title;
}