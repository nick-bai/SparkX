// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.service.vo.tool;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ToolParamsVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 字段
     */
    private String field;

    /**
     * 类型
     */
    private String type;

    /**
     * 描述
     */
    private String desc;

    /**
     * 是否必须
     */
    private Integer required;
}
