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
public class ApplicationSimpleVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
    * id
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
    * 应用的头像
    */
    private String icon;

    /**
    * 开场白
    */
    private String prologue;

    /**
    * 显示知识库引用 1:显示 2:不显示
    */
    private Integer showRelation;

    /**
    * 显示耗时 1:显示 2:不显示
    */
    private Integer showTime;

    /**
    * 显示消耗token 1:显示 2:不显示
    */
    private Integer showTokens;

    /**
    * 显示评价 1:显示 2:不显示
    */
    private Integer showAppraise;
}