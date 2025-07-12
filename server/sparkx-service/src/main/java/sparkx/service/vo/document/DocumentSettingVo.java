// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.service.vo.document;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class DocumentSettingVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文档ids
     */
    private String documentIds;

    /**
     * 命中类型 model 模型优化 direct 直接回答
     */
    private String answerType;

    /**
     * 相似度
     */
    private double redirectSimilar;
}