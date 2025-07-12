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
@TableName("public.knowledge_dataset")
public class KnowledgeDatasetEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
    * uuid
    */
    @TableId(value = "dataset_id")
    private String datasetId;

    /**
    * 知识库标题
    */
    @TableField(value = "title")
    private String title;

    /**
    * 知识库描述
    */
    @TableField(value = "description")
    private String description;

    /**
    * 创建人id
    */
    @TableField(value = "user_id")
    private String userId;

    /**
    * 类型 1:通用 2:web站点
    */
    @TableField(value = "type")
    private Integer type;

    /**
    * 模型的uuid
    */
    @TableField(value = "embedding_model_id")
    private String embeddingModelId;

    /**
     * 模型名
     */
    @TableField(value = "embedding_model")
    private String embeddingModel;

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