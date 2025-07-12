// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.service.vo.workflow;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class NextAnswerNodeVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 是否是回复节点
     */
    private boolean nodeIsAnswer;

    /**
     * 回复类型 1:本节点输出内容 2:其他回复内容
     */
    private int answerType;
}