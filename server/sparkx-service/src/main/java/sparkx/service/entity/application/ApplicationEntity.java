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

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("public.application")
public class ApplicationEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @TableId(value="app_id")
    @TableField(value = "app_id")
    private String appId;

    /**
     * 访问token
     */
    @TableField(value = "access_token")
    private String accessToken;

    /**
    * 应用名称
    */
    @TableField(value = "name")
    private String name;

    /**
    * 应用描述
    */
    @TableField(value = "description")
    private String description;

    /**
    * 应用的头像
    */
    @TableField(value = "icon")
    private String icon;

    /**
    * 使用的模型
    */
    @TableField(value = "model_id")
    private String modelId;

    /**
     * 使用的模型名
     */
    @TableField(value = "model_name")
    private String modelName;

    /**
    * 提示词
    */
    @TableField(value = "prompt")
    private String prompt;

    /**
    * 是否关联的知识库 1:关联 2:不关联
    */
    @TableField(value = "relation_dataset")
    private Integer relationDataset;

    /**
    * 开场白
    */
    @TableField(value = "prologue")
    private String prologue;

    /**
    * 显示知识库引用 1:显示 2:不显示
    */
    @TableField(value = "show_relation")
    private Integer showRelation;

    /**
    * 显示耗时 1:显示 2:不显示
    */
    @TableField(value = "show_time")
    private Integer showTime;

    /**
    * 显示消耗token 1:显示 2:不显示
    */
    @TableField(value = "show_tokens")
    private Integer showTokens;

    /**
    * 显示评价 1:显示 2:不显示
    */
    @TableField(value = "show_appraise")
    private Integer showAppraise;

    /**
    * 创建人id
    */
    @TableField(value = "user_id")
    private String userId;

    /**
    * 显示思考过程 1:显示 2:不显示
    */
    @TableField(value = "show_think")
    private Integer showThink;

    /**
    * 语音输入 1:开启 2:关闭
    */
    @TableField(value = "voice_input")
    private Integer voiceInput;

    /**
    * 语音播放 1:开启 2:关闭
    */
    @TableField(value = "voice_out")
    private Integer voiceOut;

    /**
    * 空搜索回复 1:AI 2:人工
    */
    @TableField(value = "empty_reply")
    private Integer emptyReply;

    /**
    * 空搜索回复内容
    */
    @TableField(value = "reply_content")
    private String replyContent;

    /**
    * 搜索模式：embedding,text,mix
    */
    @TableField(value = "search_mode")
    private String searchMode;

    /**
    * 相似度
    */
    @TableField(value = "similarity")
    private BigDecimal similarity;

    /**
    * 召回数量
    */
    @TableField(value = "top_rank")
    private Integer topRank;

    /**
    * 重排索引模型
    */
    @TableField(value = "rerank_model_id")
    private String rerankModelId;

    /**
    * 记忆条数
    */
    @TableField(value = "memory_num")
    private Integer memoryNum;

    /**
    * 回复上限
    */
    @TableField(value = "max_reply_token")
    private Integer maxReplyToken;

    /**
    * 回复温度
    */
    @TableField(value = "temperature")
    private double temperature;

    /**
    * 类型 1:普通 2:编排
    */
    @TableField(value = "type")
    private Integer type;

    /**
     * 问题优化
     */
    @TableField(value = "compressing_query")
    private Integer compressingQuery;

    /**
     * 状态 1:待发布 2:已发布
     */
    @TableField(value = "status")
    private Integer status;

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