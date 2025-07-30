// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.service.helper;

import cn.hutool.core.date.TimeInterval;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import dev.langchain4j.model.chat.response.PartialThinking;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.tool.ToolExecution;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import sparkx.common.constant.SparkXConstant;
import sparkx.common.utils.Tool;
import sparkx.service.extend.workflow.SendEndCallback;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public class SseEmitterHelper {

    @Autowired
    ApplicationHelper applicationHelper;

    /**
     * 异步发送给客户端
     * @param tokenStream TokenStream
     * @param emitter SseEmitter
     * @param runtimeId long
     * @param nodeId String
     */
    @Async
    public void asyncSend2Client(TokenStream tokenStream, SseEmitter emitter, long runtimeId, String nodeId) {

        // 消息开始
        sendStartSse(emitter);
        AtomicBoolean hasReasoningContent = new AtomicBoolean(false); // 是否有思考过程
        AtomicBoolean hasSendStart = new AtomicBoolean(false); // 是有发送了思考开始标识
        AtomicBoolean hasSendEnd = new AtomicBoolean(false); // 是否发送了思考结束标识

        final TimeInterval timer = new TimeInterval();
        tokenStream
                // 整理并转换召回的片段数据，返回前端
                .onRetrieved((retrievedList) -> {
                    List<Map<String, Object>> retiredMapList = new ArrayList<>();
                    retrievedList.forEach(item -> {
                        Map<String, Object> map = new HashMap<>();
                        JSONObject jsonObject = JSONUtil.parseObj(item.metadata());
                        map.put("text", item.textSegment().text());
                        map.put("embeddingId", jsonObject.get("EMBEDDING_ID"));
                        map.put("score", jsonObject.get("SCORE"));

                        retiredMapList.add(map);
                    });

                    // 召回知识库片段
                    sendMetaSse(emitter, retiredMapList);
                })
                // 思考过程
                .onPartialThinking((PartialThinking reasoningContent) -> {
                    try {
                        hasReasoningContent.set(true);

                        if (!hasSendStart.get()) {
                            emitter.send(Tool.buildSendData(runtimeId, nodeId, "<think>"));
                            hasSendStart.set(true);
                        }

                        sendSseData(reasoningContent.text(), emitter, runtimeId, nodeId);
                    } catch (Exception e) {
                        emitter.completeWithError(e);
                    }
                })
                // 工具调用
                .onToolExecuted((ToolExecution toolExecution) -> {
                    sendToolSse(emitter, toolExecution.request().name());
                })
                .onPartialResponse((content) -> {
                    try {
                        if (hasReasoningContent.get() && !hasSendEnd.get()) {
                            emitter.send(Tool.buildSendData(runtimeId, nodeId, "</think>"));
                            hasSendEnd.set(true);
                        }

                        sendSseData(content, emitter, runtimeId, nodeId);
                    } catch (Exception e) {
                        emitter.completeWithError(e);
                    }
                })
                .onCompleteResponse((response) -> {
                    // 输入的token
                    int inputTokenCount = response.tokenUsage().totalTokenCount();
                    // 输出的token
                    int outputTokenCount = response.tokenUsage().outputTokenCount();
                    // 计算耗时
                    long second = timer.intervalSecond();

                    // 发送结束信号
                    Map<String, Object> resMap = new HashMap<>();
                    resMap.put("inputTokens", inputTokenCount);
                    resMap.put("outputTokens", outputTokenCount);
                    resMap.put("totalTokens", response.tokenUsage().totalTokenCount());
                    resMap.put("time", second);
                    sendEndSse(emitter, JSONUtil.toJsonStr(resMap));

                    // 关闭sse
                    emitter.complete();
                })
                .onError(Throwable::printStackTrace)
                .start();
    }

    /**
     * 发送给客户端
     * @param tokenStream TokenStream
     * @param emitter SseEmitter
     * @param runtimeId long
     * @param nodeId String
     * @param needSend boolean
     * @param sendEndCallback SendEndCallback
     */
    @Async
    public void asyncSend2Client(TokenStream tokenStream, SseEmitter emitter, long runtimeId, String nodeId,
                                 boolean needSend, SendEndCallback sendEndCallback) {

        AtomicBoolean hasReasoningContent = new AtomicBoolean(false); // 是否有思考过程
        AtomicBoolean hasSendStart = new AtomicBoolean(false); // 是有发送了思考开始标识
        AtomicBoolean hasSendEnd = new AtomicBoolean(false); // 是否发送了思考结束标识

        tokenStream
                // 思考过程
                .onPartialThinking((PartialThinking reasoningContent) -> {
                    if (needSend) {
                        try {
                            hasReasoningContent.set(true);

                            if (!hasSendStart.get()) {
                                emitter.send(Tool.buildSendData(runtimeId, nodeId, "<think>"));
                                hasSendStart.set(true);
                            }

                            sendSseData(reasoningContent.text(), emitter, runtimeId, nodeId);
                        } catch (Exception e) {
                            emitter.completeWithError(e);
                        }
                    }
                })
                // 工具调用
                .onToolExecuted((ToolExecution toolExecution) -> {
                    sendToolSse(emitter, toolExecution.request().name());
                })
                .onPartialResponse((content) -> {
                    if (needSend) {
                        try {
                            if (hasReasoningContent.get() && !hasSendEnd.get()) {
                                emitter.send(Tool.buildSendData(runtimeId, nodeId, "</think>"));
                                hasSendStart.set(true);
                            }

                            sendSseData(content, emitter, runtimeId, nodeId);
                        } catch (Exception e) {
                            emitter.completeWithError(e);
                        }
                    }
                })
                .onCompleteResponse((response) -> {
                    // 输入的token
                    int inputTokenCount = response.tokenUsage().totalTokenCount();
                    // 输出的token
                    int outputTokenCount = response.tokenUsage().outputTokenCount();

                    // 发送结束信号
                    Map<String, Object> resMap = new HashMap<>();
                    resMap.put("inputTokenCount", inputTokenCount);
                    resMap.put("outputTokenCount", outputTokenCount);
                    resMap.put("totalTokenCount", response.tokenUsage().totalTokenCount());
                    resMap.put("content", response.aiMessage().text());

                    sendEndCallback.accept(JSONUtil.toJsonStr(resMap));
                })
                .onError(Throwable::printStackTrace)
                .start();
    }

    /**
     * 发送sse函数调用信号
     * @param sseEmitter SseEmitter
     * @param resVo String
     */
    public void sendToolSse(SseEmitter sseEmitter, String resVo) {

        try {

            sseEmitter.send(SseEmitter.event().name(SparkXConstant.SSEEventName.TOOL)
                    .data(resVo));
        } catch (IOException e) {
            //log.error("startSse error", e);
            sseEmitter.completeWithError(e);
        }
    }

    /**
     * 发送sse开始信号
     * @param sseEmitter SseEmitter
     */
    public void sendStartSse(SseEmitter sseEmitter) {

        try {

            sseEmitter.send(SseEmitter.event().name(SparkXConstant.SSEEventName.START));
        } catch (IOException e) {
            //log.error("startSse error", e);
            sseEmitter.completeWithError(e);
        }
    }

    /**
     * 发送sse结束信号
     * @param sseEmitter SseEmitter
     * @param resVo String
     */
    public void sendEndSse(SseEmitter sseEmitter, String resVo) {

        try {

            sseEmitter.send(SseEmitter.event().name(SparkXConstant.SSEEventName.DONE)
                    .data(resVo));
        } catch (IOException e) {
            //log.error("startSse error", e);
            sseEmitter.completeWithError(e);
        }
    }

    /**
     * 发送召回数据
     * @param sseEmitter SseEmitter
     * @param metaData List<Map<String, Object>>
     */
    public void sendMetaSse(SseEmitter sseEmitter, List<Map<String, Object>> metaData) {

        try {

            sseEmitter.send(SseEmitter.event().name(SparkXConstant.SSEEventName.META)
                    .data(metaData));
        } catch (IOException e) {
            //log.error("startSse error", e);
            sseEmitter.completeWithError(e);
        }
    }

    /**
     * 发送sse错误信号
     * @param sseEmitter SseEmitter
     * @param msg String
     */
    public void sendErrorSse(SseEmitter sseEmitter, String msg) {

        try {

            sseEmitter.send(SseEmitter.event().name(SparkXConstant.SSEEventName.ERROR).data(msg));
        } catch (IOException e) {
            //log.error("startSse error", e);
            sseEmitter.completeWithError(e);
        }
    }

    /**
     * 发送模型返回数据
     * @param content String
     * @param emitter SseEmitter
     * @param runtimeId Long
     * @param nodeId String
     */
    private void sendSseData(String content, SseEmitter emitter, Long runtimeId, String nodeId) {
        // 加空格配合前端的fetchEventSource进行解析，
        // 见https://github.com/Azure/fetch-event-source/blob/45ac3cfffd30b05b79fbf95c21e67d4ef59aa56a/src/parse.ts#L129-L133
        try {

            String[] lines = content.split("[\\n]", -1);
            if (lines.length > 1) {

                emitter.send(Tool.buildSendData(runtimeId, nodeId, lines[0]));

                for (int i = 1; i < lines.length; i++) {
                    /**
                     * 当响应结果的content中包含有多行文本时，
                     * 前端的fetch-event-source框架的BUG会将包含有换行符的那一行内容替换为空字符串，
                     * 故需要先将换行符与后面的内容拆分并转成，前端碰到换行标志时转成换行符处理
                     */
                    emitter.send(Tool.buildSendData(runtimeId, nodeId, "-_-_wrap_-_-"));
                    emitter.send(Tool.buildSendData(runtimeId, nodeId, lines[i]));
                }
            } else {

                emitter.send(Tool.buildSendData(runtimeId, nodeId, content));
            }

        } catch (IOException e) {
            //log.error("拆解AI返回信息失败：", e);
            sendErrorSse(emitter, e.getMessage());
            //emitter.complete();
        }
    }
}