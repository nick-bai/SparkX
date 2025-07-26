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

class RerankRequest {

    private String model;
    private String query;
    private List<String> documents;

    RerankRequest(String model, String query, List<String> documents) {
        this.model = model;
        this.query = query;
        this.documents = documents;
    }

    public static RerankRequestBuilder builder() {
        return new RerankRequestBuilder();
    }

    public String getModel() {
        return this.model;
    }

    public String getQuery() {
        return this.query;
    }

    public List<String> getDocuments() {
        return this.documents;
    }

    public static class RerankRequestBuilder {
        private String model;
        private String query;
        private List<String> documents;

        RerankRequestBuilder() {
        }

        public RerankRequestBuilder model(String model) {
            this.model = model;
            return this;
        }

        public RerankRequestBuilder query(String query) {
            this.query = query;
            return this;
        }

        public RerankRequestBuilder documents(List<String> documents) {
            this.documents = documents;
            return this;
        }

        public RerankRequest build() {
            return new RerankRequest(this.model, this.query, this.documents);
        }

        public String toString() {
            return "RerankRequest.RerankRequestBuilder(model=" + this.model + ", query=" + this.query + ", documents=" + this.documents + ")";
        }
    }
}
