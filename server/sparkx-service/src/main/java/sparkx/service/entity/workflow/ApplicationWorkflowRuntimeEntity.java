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
@TableName("public.application_workflow_runtime")
public class ApplicationWorkflowRuntimeEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
    * 主键
    */
    @TableId(value="id", type= IdType.AUTO)
    @TableField(value = "id")
    private Long id;

    /**
    * 用户id
    */
    @TableField(value = "user_id")
    private String userId;

    /**
    * 关联的流程id
    */
    @TableField(value = "flow_id")
    private long flowId;

    /**
    * 首个问题
    */
    @TableField(value = "title")
    private String title;

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