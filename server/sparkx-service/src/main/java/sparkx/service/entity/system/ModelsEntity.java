// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.service.entity.system;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("public.models")
public class ModelsEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 模型id
     */
    @TableId("model_id")
    @TableField(value = "model_id")
    private String modelId;

    /**
     * 模型名
     */
    @TableField(value = "name")
    private String name;

    /**
     * 模型标识
     */
    @TableField(value = "model_flag")
    private String modelFlag;

    /**
     * 类型 1:语言模型 2:向量模型 3:重排模型
     */
    @TableField(value = "type")
    private String type;

    /**
     * 验证信息
     */
    @TableField(value = "credential")
    private String credential;

    /**
     * 配置参数
     */
    @TableField(value = "options")
    private String options;

    /**
     * 状态 1:正常 2:禁用
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 可用模型
     */
    @TableField(value = "models")
    private String models;

    /**
     * 图标
     */
    @TableField(value = "icon")
    private String icon;

    /**
     * 支持函数调用的模型
     */
    @TableField(value = "function_calling")
    private String functionCalling;

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
