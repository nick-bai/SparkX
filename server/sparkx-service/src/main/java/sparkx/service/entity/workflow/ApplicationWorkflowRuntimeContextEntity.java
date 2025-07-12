// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.service.entity.workflow;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("public.application_workflow_runtime_context")
public class ApplicationWorkflowRuntimeContextEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
    * 主键
    */
    @TableId(value="id", type= IdType.AUTO)
    @TableField(value = "id")
    private Long id;

    /**
    * 运行时id
    */
    @TableField(value = "runtime_id")
    private Long runtimeId;

    /**
    * 节点类型
    */
    @TableField(value = "node_type")
    private String nodeType;

    /**
    * 步骤号
    */
    @TableField(value = "step")
    private Integer step;

    /**
    * 出参数据
    */
    @TableField(value = "output_data")
    private String outputData;

    /**
    * 模型数据
    */
    @TableField(value = "model_data")
    private String modelData;

    /**
     * 节点id
     */
    @TableField(value = "cell")
    private String cell;

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