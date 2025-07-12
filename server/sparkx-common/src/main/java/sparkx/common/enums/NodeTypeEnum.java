// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.common.enums;

public enum NodeTypeEnum {

    AGENT("agent-node", "智能体节点"),
    ANSWER("answer-node", "回复节点"),
    DATASET("dataset-node", "知识库节点"),
    LLM("llm-node", "大模型节点"),
    PURPOSE("purpose-node", "意图分类节点"),
    SWITCH("switch-node", "分支节点");

    /**
     * 构造方法
     */
    private final String code;
    private final String msg;

    NodeTypeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }


    /**
     * 获取状态码
     *
     * @author fzr
     * @return Long
     */
    public String getCode() {
        return this.code;
    }

    /**
     * 获取提示
     *
     * @author fzr
     * @return String
     */
    public String getMsg() {
        return this.msg;
    }
}
