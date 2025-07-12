// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.service.entity.application;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("public.application_chat_log")
public class ApplicationChatLogEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @TableId(type = IdType.AUTO)
    @TableField(value = "log_id")
    private Integer logId;

    /**
    * 所属应用id
    */
    @TableField(value = "app_id")
    private String appId;

    /**
     * 用户id
     */
    @TableField(value = "user_id")
    private String userId;

    /**
    * 所属对话id
    */
    @TableField(value = "session_id")
    private String sessionId;

    /**
    * 问题
    */
    @TableField(value = "question")
    private String question;

    /**
    * 内容
    */
    @TableField(value = "content")
    private String content;

    /**
    * 消耗时间
    */
    @TableField(value = "time")
    private Integer time;

    /**
    * 消耗的token
    */
    @TableField(value = "tokens")
    private Integer tokens;

    /**
    * 引用的知识库
    */
    @TableField(value = "retrieved_list")
    private String retrievedList;

    /**
    * 评价 1:好评 2:差评
    */
    @TableField(value = "appraise")
    private Integer appraise;

    /**
     * 工具使用
     */
    @TableField(value = "tool_use")
    private String toolUse;

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

    /**
     * 日期
     */
    @TableField(exist = false)
    private String date;

    /**
     * 聚合数据
     */
    @TableField(exist = false)
    private long totalData;
}