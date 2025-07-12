// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.service.vo.application;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ApplicationListVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 应用id
     */
    private String appId;

    /**
     * 访问token
     */
    private String accessToken;

    /**
     * 应用名称
     */
    private String name;

    /**
     * 应用描述
     */
    private String description;

    /**
     * 应用类型 1:普通  2:编排
     */
    private Integer type;

    /**
     * 创建人
     */
    private String author;

    /**
     * 图标
     */
    private String icon;

    /**
     * 查看权限
     */
    private boolean view;

    /**
     * 管理权限
     */
    private boolean manage;

    /**
     * 状态 1:未发布 2:已发布
     */
    private Integer status;
}
