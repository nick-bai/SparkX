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
import java.util.List;

@Data
public class ApplicationLogVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 记录id
     */
    private Integer logId;

    /**
     * 应用id
     */
    private String appId;

    /**
     * 用户问题
     */
    private String question;

    /**
     * ai回答
     */
    private List<AnswerVo> answer;

    /**
     * 会话id
     */
    private String sessionId;

    /**
     * 耗时
     */
    private Integer time;

    /**
     * 输入token数
     */
    private Integer inputTokens;

    /**
     * 输出token数
     */
    private Integer outputTokens;

    /**
     * 消耗token数
     */
    private Integer totalTokens;

    /**
     * 召回的
     */
    private String retrievedList;

    /**
     * 评价信息
     */
    private Integer appraise;

    /**
     * 使用的工具
     */
    private String toolUse;
}
