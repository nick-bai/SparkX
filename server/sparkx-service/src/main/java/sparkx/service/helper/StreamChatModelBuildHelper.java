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

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import dev.langchain4j.community.model.zhipu.ZhipuAiStreamingChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sparkx.service.entity.application.ApplicationEntity;
import sparkx.service.entity.system.ModelsEntity;

import java.time.Duration;
import java.util.List;

@Component
public class StreamChatModelBuildHelper {

    private ModelsEntity modelInfo;

    private ApplicationEntity applicationInfo;

    @Autowired
    ApplicationHelper applicationHelper;

    /**
     * 构建流输出model
     * @param modelInfo ModelsEntity
     * @param applicationInfo ApplicationEntity
     * @return StreamingChatModel
     */
    public StreamingChatModel build(ModelsEntity modelInfo, ApplicationEntity applicationInfo) {

        this.modelInfo = modelInfo;
        this.applicationInfo = applicationInfo;

        // 清华智普
        if (modelInfo.getModelFlag().equals("zhipu")) {
            return buildZhiPu();
        }

        // 构建ollama
        if (modelInfo.getModelFlag().equals("ollama")) {
            return buildOllama();
        }

        // 千帆、千问、豆包、GPT
        return buildOpenAI();
    }

    /**
     * 构建智普
     * @return StreamingChatModel
     */
    private StreamingChatModel buildZhiPu() {

        JSONArray jsonConfig = JSONUtil.parseArray(modelInfo.getCredential());
        String key = jsonConfig.getJSONObject(0).getStr("value");

        return ZhipuAiStreamingChatModel.builder()
                .apiKey(key)
                .temperature(applicationInfo.getTemperature()) // 温度
                .model(applicationInfo.getModelName())
                .connectTimeout(Duration.ofSeconds(60))
                .readTimeout(Duration.ofSeconds(60))
                .build();
    }

    /**
     * 构建ollama
     * @return StreamingChatModel
     */
    private StreamingChatModel buildOllama() {

        JSONArray jsonOptions = JSONUtil.parseArray(modelInfo.getOptions());
        String url = jsonOptions.getJSONObject(1).getStr("value");

        return OllamaStreamingChatModel.builder()
                .baseUrl(url)
                .modelName(applicationInfo.getModelName())
                .build();
    }

    /**
     * 通过标准openai结构构建对象
     * @return StreamingChatModel
     */
    private StreamingChatModel buildOpenAI() {

        JSONArray jsonConfig = JSONUtil.parseArray(modelInfo.getCredential());
        String key = jsonConfig.getJSONObject(0).getStr("value");

        JSONArray jsonOptions = JSONUtil.parseArray(modelInfo.getOptions());
        String url = jsonOptions.getJSONObject(1).getStr("value");

        return OpenAiStreamingChatModel.builder()
                .baseUrl(url)
                .apiKey(key)
                .returnThinking(true)
                .modelName(applicationInfo.getModelName())
                .listeners(List.of(applicationHelper.observability()))
                .build();
    }
}
