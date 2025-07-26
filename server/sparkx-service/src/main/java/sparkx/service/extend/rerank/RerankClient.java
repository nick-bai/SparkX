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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import dev.langchain4j.internal.Utils;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.net.Proxy;
import java.time.Duration;
import java.util.Objects;

import static dev.langchain4j.internal.ValidationUtils.ensureNotBlank;

public class RerankClient {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
            .enable(SerializationFeature.INDENT_OUTPUT)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private final RerankApi rerankApi;
    private final String authorizationHeader;

    RerankClient(String baseUrl, String apiKey, Duration timeout, Proxy proxy, Boolean logRequests, Boolean logResponses, Boolean needBearer) {

        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder()
                .callTimeout(timeout)
                .connectTimeout(timeout)
                .readTimeout(timeout)
                .writeTimeout(timeout);

        if (logRequests) {
            okHttpClientBuilder.addInterceptor(new RequestLoggingInterceptor());
        }

        if (logResponses) {
            okHttpClientBuilder.addInterceptor(new ResponseLoggingInterceptor());
        }

        if (Objects.nonNull(proxy)) {
            okHttpClientBuilder.proxy(proxy);
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Utils.ensureTrailingForwardSlash(baseUrl))
                .client(okHttpClientBuilder.build())
                .addConverterFactory(JacksonConverterFactory.create(OBJECT_MAPPER))
                .build();

        this.rerankApi = retrofit.create(RerankApi.class);
        if (needBearer) {
            this.authorizationHeader = "Bearer " + ensureNotBlank(apiKey, "apiKey");
        } else {
            this.authorizationHeader = ensureNotBlank(apiKey, "apiKey");
        }
    }

    public static RerankClientBuilder builder() {
        return new RerankClientBuilder();
    }

    RerankResponse rerank(RerankRequest request) {
        try {

            retrofit2.Response<RerankResponse> retrofitResponse;
            retrofitResponse = rerankApi.rerank(request, authorizationHeader).execute();

            if (retrofitResponse.isSuccessful()) {
                return retrofitResponse.body();
            } else {
                throw toException(retrofitResponse);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static RuntimeException toException(retrofit2.Response<?> response) throws IOException {
        int code = response.code();
        String body = response.errorBody().string();
        String errorMessage = String.format("status code: %s; body: %s", code, body);
        return new RuntimeException(errorMessage);
    }

    public static class RerankClientBuilder {
        private String baseUrl;
        private String apiKey;
        private Duration timeout;
        private Proxy proxy;
        private Boolean logRequests;
        private Boolean logResponses;
        private Boolean needBearer;

        RerankClientBuilder() {
        }

        public RerankClientBuilder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public RerankClientBuilder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public RerankClientBuilder timeout(Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        public RerankClientBuilder proxy(Proxy proxy) {
            this.proxy = proxy;
            return this;
        }

        public RerankClientBuilder logRequests(Boolean logRequests) {
            this.logRequests = logRequests;
            return this;
        }

        public RerankClientBuilder logResponses(Boolean logResponses) {
            this.logResponses = logResponses;
            return this;
        }

        public RerankClientBuilder needBearer(Boolean needBearer) {
            this.needBearer = needBearer;
            return this;
        }

        public RerankClient build() {
            return new RerankClient(this.baseUrl, this.apiKey, this.timeout, this.proxy, this.logRequests, this.logResponses, this.needBearer);
        }

        public String toString() {
            return "RerankClient.RerankClientBuilder(baseUrl=" + this.baseUrl + ", apiKey=" + this.apiKey + ", timeout="
                    + this.timeout + ", proxy=" + this.proxy + ", logRequests=" + this.logRequests
                    + ", logResponses=" + this.logResponses + ", needBearer=" + this.needBearer + ")";
        }
    }
}
