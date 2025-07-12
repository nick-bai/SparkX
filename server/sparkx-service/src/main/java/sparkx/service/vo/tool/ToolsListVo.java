// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.service.vo.tool;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ToolsListVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 插件id
     */
    private Integer id;

    /**
     * 插件标识
     */
    private String name;

    /**
     * 插件名称
     */
    private String title;

    /**
     * 插件描述
     */
    private String description;

    /**
     * 接口地址
     */
    private String apiUrl;

    /**
     * 鉴权类型 1:无鉴权 2:api key
     */
    private Integer authType;

    /**
     * 秘钥位置 1:header 2:body
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
     * 请求参数
     */
    private String postParams;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}