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

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.output.TokenUsage;
import dev.langchain4j.model.scoring.ScoringModel;

import java.net.Proxy;
import java.time.Duration;
import java.util.List;

import static dev.langchain4j.internal.RetryUtils.withRetryMappingExceptions;
import static dev.langchain4j.internal.Utils.getOrDefault;
import static dev.langchain4j.internal.ValidationUtils.ensureNotBlank;
import static java.time.Duration.ofSeconds;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.toList;

public class RerankScoringModel implements ScoringModel {

    private static final String DEFAULT_BASE_URL = "";

    private final RerankClient client;
    private final String modelName;
    private final Integer maxRetries;

    public RerankScoringModel(
            String baseUrl,
            String apiKey,
            String modelName,
            Duration timeout,
            Integer maxRetries,
            Proxy proxy,
            Boolean logRequests,
            Boolean logResponses,
            Boolean needBearer) {
        this.client = RerankClient.builder()
                .baseUrl(getOrDefault(baseUrl, DEFAULT_BASE_URL))
                .apiKey(ensureNotBlank(apiKey, "apiKey"))
                .timeout(getOrDefault(timeout, ofSeconds(60)))
                .proxy(proxy)
                .logRequests(getOrDefault(logRequests, false))
                .logResponses(getOrDefault(logResponses, false))
                .needBearer(getOrDefault(needBearer, true))
                .build();
        this.modelName = modelName;
        this.maxRetries = getOrDefault(maxRetries, 2);
    }

    public static RerankScoringModelBuilder builder() {
        return new RerankScoringModelBuilder();
    }

    @Override
    public Response<List<Double>> scoreAll(List<TextSegment> segments, String query) {

        RerankRequest request = RerankRequest.builder()
                .model(modelName)
                .query(query)
                .documents(segments.stream()
                        .map(TextSegment::text)
                        .collect(toList()))
                .build();

        RerankResponse response = withRetryMappingExceptions(() -> client.rerank(request), maxRetries);

        List<Double> scores = response.getResults().stream()
                .sorted(comparingInt(Result::getIndex))
                .map(Result::getRelevanceScore)
                .collect(toList());

        return Response.from(scores, new TokenUsage(response.getUsage().getTotalTokens()));
    }

    public static class RerankScoringModelBuilder {
        private String baseUrl;
        private String apiKey;
        private String modelName;
        private Duration timeout;
        private Integer maxRetries;
        private Proxy proxy;
        private Boolean logRequests;
        private Boolean logResponses;
        private Boolean needBearer;
        private String modelFlag;

        RerankScoringModelBuilder() {
        }

        public RerankScoringModelBuilder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public RerankScoringModelBuilder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public RerankScoringModelBuilder modelName(String modelName) {
            this.modelName = modelName;
            return this;
        }

        public RerankScoringModelBuilder timeout(Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        public RerankScoringModelBuilder maxRetries(Integer maxRetries) {
            this.maxRetries = maxRetries;
            return this;
        }

        public RerankScoringModelBuilder proxy(Proxy proxy) {
            this.proxy = proxy;
            return this;
        }

        public RerankScoringModelBuilder logRequests(Boolean logRequests) {
            this.logRequests = logRequests;
            return this;
        }

        public RerankScoringModelBuilder logResponses(Boolean logResponses) {
            this.logResponses = logResponses;
            return this;
        }

        public RerankScoringModelBuilder needBearer(Boolean needBearer) {
            this.needBearer = needBearer;
            return this;
        }

        public RerankScoringModelBuilder modelFlag(String modelFlag) {
            this.modelFlag = modelFlag;
            return this;
        }

        public RerankScoringModel build() {
            return new RerankScoringModel(this.baseUrl, this.apiKey, this.modelName, this.timeout, this.maxRetries, this.proxy,
                    this.logRequests, this.logResponses, this.needBearer);
        }

        public String toString() {
            return "RerankScoringModel.RerankScoringModelBuilder(baseUrl=" + this.baseUrl + ", apiKey=" + this.apiKey + ", modelName="
                    + this.modelName + ", timeout=" + this.timeout + ", maxRetries=" + this.maxRetries + ", proxy=" + this.proxy
                    + ", logRequests=" + this.logRequests + ", logResponses=" + this.logResponses +
                    ", needBearer=" + this.needBearer + ")";
        }
    }
}
