// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.service.vo.question;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class QuestionRelationListVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 关联的知识库
     */
    private String datasetId;

    /**
     * 关联的文档
     */
    private String documentId;

    /**
     * 关联的段落
     */
    private String paragraphId;

    /**
     * 关联的问题
     */
    private String questionId;
}