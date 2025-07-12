// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.service.validate.application;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ApplicationAddValidate implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 应用id
     */
    private String appId;

    /**
     * 应用名称
     */
    @Length(min = 2, max = 25, message = "应用标题必须在2到25个字")
    @NotEmpty(message = "应用标题不能为空")
    private String name;

    /**
     * 应用描述
     */
    @Length(min = 2, max = 25, message = "应用描述必须在2到255个字")
    @NotEmpty(message = "应用描述不能为空")
    private String description;

    /**
     * 应用类型
     */
    private Integer type;
}
