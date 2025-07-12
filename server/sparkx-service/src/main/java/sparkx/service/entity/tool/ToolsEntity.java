// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.service.entity.tool;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("public.tools")
public class ToolsEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @TableId(value="id", type= IdType.AUTO)
    @TableField(value = "id")
    private Integer id;

    /**
    * 插件标识
    */
    @TableField(value = "name")
    private String name;

    /**
    * 插件名称
    */
    @TableField(value = "title")
    private String title;

    /**
    * 插件描述
    */
    @TableField(value = "description")
    private String description;

    /**
    * 接口地址
    */
    @TableField(value = "api_url")
    private String apiUrl;

    /**
    * 鉴权类型 1:无鉴权 2:api key
    */
    @TableField(value = "auth_type")
    private Integer authType;

    /**
    * 秘钥位置 1:header 2:body
    */
    @TableField(value = "auth_way")
    private Integer authWay;

    /**
    * 秘钥名称
    */
    @TableField(value = "api_key_name")
    private String apiKeyName;

    /**
    * 秘钥值
    */
    @TableField(value = "api_key_value")
    private String apiKeyValue;

    /**
    * 请求参数
    */
    @TableField(value = "post_params")
    private String postParams;

    /**
     * 插件类型 1:自定义 2:MCP
     */
    @TableField(value = "type")
    private Integer type;

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