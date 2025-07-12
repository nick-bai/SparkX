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
@TableName("public.knowledge_paragraph")
public class KnowledgeParagraphEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
    * 唯一标识
    */
    @TableId(value = "paragraph_id")
    private String paragraphId;

    /**
    * 段落标题
    */
    @TableField(value = "title")
    private String title;

    /**
    * 段落内容
    */
    @TableField(value = "content")
    private String content;

    /**
    * 知识库id
    */
    @TableField(value = "dataset_id")
    private String datasetId;

    /**
    * 文档id
    */
    @TableField(value = "document_id")
    private String documentId;

    /**
    * 状态 1:待索引 2:索引中 3:索引完成
    */
    @TableField(value = "status")
    private Integer status;

    /**
    * 状态 1:正常 2:禁用
    */
    @TableField(value = "active")
    private Integer active;

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