// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.service.entity.dataset;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("public.knowledge_question_paragraph")
public class KnowledgeQuestionParagraphEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 唯一标识
     */
    @TableId(value = "uuid")
    private String uuid;

    /**
    * 关联的知识库
    */
    @TableField(value = "dataset_id")
    private String datasetId;

    /**
    * 关联的文档
    */
    @TableField(value = "document_id")
    private String documentId;

    /**
    * 关联的段落
    */
    @TableField(value = "paragraph_id")
    private String paragraphId;

    /**
    * 关联的问题
    */
    @TableField(value = "question_id")
    private String questionId;

    /**
    * 创建时间
    */
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    /**
    * 更新时间
    */
    @TableField(value = "update_time")
    private LocalDateTime updateTime;
}