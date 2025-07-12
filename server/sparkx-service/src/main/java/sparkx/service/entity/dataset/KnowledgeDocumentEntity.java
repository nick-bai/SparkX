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
@TableName("public.knowledge_document")
public class KnowledgeDocumentEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 唯一标识
     */
    @TableId(value = "document_id")
    private String documentId;

    /**
    * 文件名称
    */
    @TableField(value = "name")
    private String name;

    /**
    * 字符长度
    */
    @TableField(value = "file_size")
    private long fileSize;

    /**
    * 状态 1:待索引 2:索引中 3:索引完成
    */
    @TableField(value = "status")
    private Integer status;

    /**
    * 生成问题状态 1:待生成 2:生成中 3:生成完成
    */
    @TableField(value = "question_status")
    private Integer questionStatus;

    /**
    * 状态 1:正常 2:禁用
    */
    @TableField(value = "active")
    private Integer active;

    /**
    * 所属知识库
    */
    @TableField(value = "dataset_id")
    private String datasetId;

    /**
    * 段落数量
    */
    @TableField(value = "paragraph_num")
    private Integer paragraphNum;

    /**
     * 上次向量化时间
     */
    @TableField(value = "embedding_time")
    private LocalDateTime embeddingTime;

    /**
     * 上次添加问题时间
     */
    @TableField(value = "question_time")
    private LocalDateTime questionTime;

    /**
     * 命中处理方式
     */
    @TableField(value = "answer_type")
    private String answerType;

    /**
     * 直接返回的相似度
     */
    @TableField(value = "redirect_similar")
    private double redirectSimilar;

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