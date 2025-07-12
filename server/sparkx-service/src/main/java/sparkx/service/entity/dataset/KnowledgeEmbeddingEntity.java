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
import sparkx.service.task.TsVectorTypeHandler;
import sparkx.service.task.VectorTypeHandler;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("public.knowledge_embedding")
public class KnowledgeEmbeddingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
    * 唯一标识
    */
    @TableId(value = "embedding_id")
    private String embeddingId;

    /**
    * 所属的知识库
    */
    @TableField(value = "dataset_id")
    private String datasetId;

    /**
    * 所属文档
    */
    @TableField(value = "document_id")
    private String documentId;

    /**
    * 所属段落
    */
    @TableField(value = "paragraph_id")
    private String paragraphId;

    /**
    * 向量数据
    */
    @TableField(value = "embedding", typeHandler= VectorTypeHandler.class)
    private List<Float> embedding;

    /**
    * 全文索引
    */
    @TableField(value = "search_vector", typeHandler= TsVectorTypeHandler.class)
    private String searchVector;

    /**
    * 状态 1:正常 2:禁用
    */
    @TableField(value = "active")
    private Integer active;

    /**
     * 来源类型 1:文档 2:问题
     */
    @TableField(value = "source_type")
    private Integer sourceType;

    /**
     * 来源id
     */
    @TableField(value = "source_id")
    private String sourceId;

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