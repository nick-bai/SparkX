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
import okio.Buffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.stream.StreamSupport.stream;

class RequestLoggingInterceptor implements Interceptor {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingInterceptor.class);

    private static final Pattern BEARER_PATTERN = Pattern.compile("(Bearer\\s)(\\w{2})(\\w+)(\\w{2})");

    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        log(request);
        return chain.proceed(request);
    }

    private void log(Request request) {
        log.debug(
                "Request:\n" +
                        "- method: {}\n" +
                        "- url: {}\n" +
                        "- headers: {}\n" +
                        "- body: {}",
                request.method(),
                request.url(),
                inOneLine(request.headers()),
                getBody(request)
        );
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

    private static String getBody(Request request) {
        try {
            Buffer buffer = new Buffer();
            request.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (Exception e) {
            log.warn("Exception happened while reading request body", e);
            return "[Exception happened while reading request body. Check logs for more details.]";
        }
    }
}
