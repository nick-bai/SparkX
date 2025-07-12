// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.service.validate.application;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import sparkx.service.vo.application.PrologueVo;
import sparkx.service.vo.dataset.DatasetSimpleVo;
import sparkx.service.vo.tool.ToolsSimpleListVo;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
public class ApplicationSaveValidate implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @NotEmpty(message = "应用id不能为空")
    private String appId;

    /**
    * 应用名称
    */
    @Length(min = 2, max = 25, message = "应用标题必须在2到25个字")
    @NotEmpty(message = "应用标题不能为空")
    private String name;

    /**
    * 应用描述
    */
    @Length(min = 2, max = 255, message = "应用标题必须在2到255个字")
    @NotEmpty(message = "应用描述不能为空")
    private String description;

    /**
    * 应用的头像
    */
    @NotEmpty(message = "应用的logo不能为空")
    private String icon;

    /**
    * 使用的模型
    */
    private String modelId;

    /**
     * 使用的模型名
     */
    private String modelName;

    /**
    * 提示词
    */
    private String prompt;

    /**
    * 开场白
    */
    private PrologueVo prologue;

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

    /**
    * 创建人id
    */
    private String userId;

    /**
    * 显示思考过程 1:显示 2:不显示
    */
    private Integer showThink;

    /**
    * 语音输入 1:开启 2:关闭
    */
    private Integer voiceInput;

    /**
    * 语音播放 1:开启 2:关闭
    */
    private Integer voiceOut;

    /**
    * 空搜索回复 1:AI 2:人工
    */
    private Integer emptyReply;

    /**
    * 空搜索回复内容
    */
    private String replyContent;

    /**
    * 搜索模式：embedding,text,mix
    */
    private String searchMode;

    /**
    * 相似度
    */
    private double similarity;

    /**
    * 召回数量
    */
    private Integer topRank;

    /**
    * 重排索引模型
    */
    private String rerankModelId;

    /**
    * 记忆条数
    */
    private Integer memoryNum;

    /**
    * 回复上限
    */
    private Long maxReplyToken;

    /**
    * 回复温度
    */
    private double temperature;

    /**
    * 类型 1:普通 2:编排
    */
    private Integer type;

    /**
     * 问题优化 1:开启 2:关闭
     */
    private Integer compressingQuery;

    /**
     * 知识库列表
     */
    private List<DatasetSimpleVo> datasetList;

    /**
     * 工具列表
     */
    private List<ToolsSimpleListVo> toolList;

    /**
     * 保存类型 1:仅保存 2:保存并发布
     */
    private Integer saveType;

    /**
     * 用户输入内容
     */
    private String content;
}