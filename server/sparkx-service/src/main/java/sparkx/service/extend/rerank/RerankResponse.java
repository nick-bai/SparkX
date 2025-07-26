// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.service.extend.rerank;

import java.util.List;

class RerankResponse {

    private List<Result> results;
    private Usage usage;

    public Usage getUsage() {
        return usage;
    }

    public List<Result> getResults() {
        return this.results;
    }
}
