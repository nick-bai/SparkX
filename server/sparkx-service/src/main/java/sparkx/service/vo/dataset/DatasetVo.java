// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.service.vo.dataset;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class DatasetVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
    * uuid
    */
    private String datasetId;

    /**
    * 知识库标题
    */
    private String title;

    /**
    * 知识库描述
    */
    private String description;

    /**
    * 类型 1:通用 2:web站点
    */
    private Integer type;

    /**
    * 模型的uuid
    */
    @JsonProperty(value = "embedding_model_id")
    private String embeddingModelId;

    /**
     * 模型的uuid
     */
    @JsonProperty(value = "embedding_model")
    private String embeddingModel;

    /**
     * 创建人
     */
    private String author;

    /**
     * 文档数
     */
    private long documentNum;

    /**
     * 字符数
     */
    private long fileSize;

    /**
     * 关联的应用数
     */
    private long appNum;

    /**
     * 查看权限
     */
    private boolean view;

    /**
     * 管理权限
     */
    private boolean manage;
}