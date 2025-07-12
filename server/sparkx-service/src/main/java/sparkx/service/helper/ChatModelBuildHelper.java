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
import dev.langchain4j.community.model.zhipu.ZhipuAiChatModel;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.stereotype.Component;
import sparkx.service.entity.application.ApplicationEntity;
import sparkx.service.entity.system.ModelsEntity;

import java.time.Duration;

@Component
public class ChatModelBuildHelper {

    private ModelsEntity modelInfo;

    private ApplicationEntity applicationInfo;

    /**
     * 构建model
     * @param modelInfo ModelsEntity
     * @param applicationInfo ApplicationEntity
     * @return ChatModel
     */
    public ChatModel build(ModelsEntity modelInfo, ApplicationEntity applicationInfo) {

        this.modelInfo = modelInfo;
        this.applicationInfo = applicationInfo;

        // 清华智普
        if (modelInfo.getModelFlag().equals("zhipu")) {
            return buildZhiPu();
        }

        // 千帆、千问、豆包、GPT
        return buildOpenAI();
    }

    /**
     * 构建智普
     * @return StreamingChatModel
     */
    private ChatModel buildZhiPu() {

        JSONArray jsonConfig = JSONUtil.parseArray(modelInfo.getCredential());
        String key = jsonConfig.getJSONObject(0).getStr("value");

        return ZhipuAiChatModel.builder()
                .apiKey(key)
                .temperature(applicationInfo.getTemperature()) // 温度
                .model(applicationInfo.getModelName())
                .connectTimeout(Duration.ofSeconds(60))
                .readTimeout(Duration.ofSeconds(60))
                .build();
    }

    /**
     * 通过标准openai结构构建对象
     * @return ChatModel
     */
    private ChatModel buildOpenAI() {

        JSONArray jsonConfig = JSONUtil.parseArray(modelInfo.getCredential());
        String key = jsonConfig.getJSONObject(0).getStr("value");

        JSONArray jsonOptions = JSONUtil.parseArray(modelInfo.getOptions());
        String url = jsonOptions.getJSONObject(1).getStr("value");

        return OpenAiChatModel.builder()
                .baseUrl(url)
                .apiKey(key)
                .modelName(applicationInfo.getModelName())
                .build();
    }
}
