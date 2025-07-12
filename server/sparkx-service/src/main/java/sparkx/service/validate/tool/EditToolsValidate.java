// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.service.validate.tool;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;

@Data
public class EditToolsValidate implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id;

    /**
     * 插件标识
     */
    @NotEmpty(message = "插件标识不能为空")
    @Length(min = 1, max = 155, message = "插件标识必须在1到155个字符")
    private String name;

    /**
     * 插件名称
     */
    @NotEmpty(message = "插件名称不能为空")
    @Length(min = 1, max = 155, message = "插件名称必须在1到155个字符")
    private String title;

    /**
     * 插件描述
     */
    @NotEmpty(message = "插件描述不能为空")
    @Length(min = 1, max = 155, message = "插件描述必须在1到155个字符")
    private String description;

    /**
     * 接口地址
     */
    @NotEmpty(message = "接口地址不能为空")
    private String apiUrl;

    /**
     * 鉴权类型
     */
    @NotEmpty(message = "鉴权类型不能为空")
    private Integer authType;

    /**
     * 秘钥放置位置 1:header 2:body
     */
    private Integer authWay;

    /**
     * 秘钥名称
     */
    private String apiKeyName;

    /**
     * 秘钥值
     */
    private String apiKeyValue;

    /**
     * 接口参数
     */
    @NotEmpty(message = "接口参数不能为空")
    private String postParams;
}