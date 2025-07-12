// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.service.validate.dataset;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;

@Data
public class DatasetValidate implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 知识库id
     */
    private String datasetId;

    /**
     * 知识库标题
     */
    @Length(min = 2, max = 25, message = "知识库标题必须在2到25个字")
    @NotEmpty(message = "知识库标题不能为空")
    private String title;

    /**
     * 知识库描述
     */
    @Length(min = 2, max = 255, message = "知识库标题必须在2到255个字")
    @NotEmpty(message = "知识库描述不能为空")
    private String description;

    /**
     * 模型id
     */
    @NotEmpty(message = "embeding模型不能为空")
    private String embedding_model_id;

    /**
     * 模型id
     */
    @NotEmpty(message = "embeding模型不能为空")
    private String embedding_model;
}