// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.service.vo.system;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ModelsInfoVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 模型id
     */
    private String modelId;

    /**
     * 模型名
     */
    private String name;

    /**
     * 模型标识
     */
    private String modelFlag;

    /**
     * 类型 1:语言模型 2:向量模型 3:重排模型
     */
    private String type;

    /**
     * 验证信息
     */
    private String credential;

    /**
     * 配置参数
     */
    private String options;

    /**
     * 状态 1:正常 2:禁用
     */
    private Integer status;

    /**
     * 可用模型
     */
    private String models;

    /**
     * 图标
     */
    private String icon;

    /**
     * 支持函数调用的模型
     */
    private String functionCalling;
}
