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
import sparkx.service.entity.tool.ToolsEntity;
import sparkx.service.vo.dataset.DatasetSimpleVo;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
public class ApplicationChatValidate implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 会听id
     */
    private String sessionId;

    /**
     * id
     */
    @NotEmpty(message = "应用id不能为空")
    private String appId;

    /**
     * 用户输入内容
     */
    private String content;

    /**
     * 知识库列表
     */
    private List<DatasetSimpleVo> datasetList;

    /**
     * 插件列表
     */
    private List<ToolsEntity> toolsList;

    /**
     * 运行时上下文id
     */
    private long contextId;

    /**
     * 运行节点id
     */
    private String cell;
}