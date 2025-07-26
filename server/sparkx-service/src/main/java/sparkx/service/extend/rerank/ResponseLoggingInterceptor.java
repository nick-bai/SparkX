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

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.stream.StreamSupport.stream;

class ResponseLoggingInterceptor implements Interceptor {

    private static final Pattern BEARER_PATTERN = Pattern.compile("(Bearer\\s)(\\w{2})(\\w+)(\\w{2})");

    private static final Logger log = LoggerFactory.getLogger(ResponseLoggingInterceptor.class);

    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        log(response);
        return response;
    }

    void log(Response response) {
        log.debug(
                "Response:\n" +
                        "- status code: {}\n" +
                        "- headers: {}\n" +
                        "- body: {}",
                response.code(),
                inOneLine(response.headers()),
                getBody(response)
        );
    }

    private String getBody(Response response) {
        try {
            return response.peekBody(Long.MAX_VALUE).string();
        } catch (IOException e) {
            log.warn("Failed to log response", e);
            return "[failed to log response]";
        }
    }

    static String inOneLine(Headers headers) {
        return stream(headers.spliterator(), false)
                .map((header) -> {
                    String headerKey = header.component1();
                    String headerValue = header.component2();
                    if (headerKey.equals("Authorization")) {
                        headerValue = maskAuthorizationHeaderValue(headerValue);
                    }
                    return String.format("[%s: %s]", headerKey, headerValue);
                }).collect(Collectors.joining(", "));
    }

    private static String maskAuthorizationHeaderValue(String authorizationHeaderValue) {
        try {
            Matcher matcher = BEARER_PATTERN.matcher(authorizationHeaderValue);
            StringBuffer sb = new StringBuffer();

            while (matcher.find()) {
                matcher.appendReplacement(sb, matcher.group(1) + matcher.group(2) + "..." + matcher.group(4));
            }

            matcher.appendTail(sb);
            return sb.toString();
        } catch (Exception e) {
            return "[failed to mask the API key]";
        }
    }
}
