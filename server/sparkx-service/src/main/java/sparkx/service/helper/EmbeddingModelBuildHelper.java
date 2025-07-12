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
import dev.langchain4j.community.model.zhipu.ZhipuAiEmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sparkx.service.entity.dataset.KnowledgeDatasetEntity;
import sparkx.service.entity.system.ModelsEntity;
import sparkx.service.mapper.system.ModelsMapper;

import java.util.HashMap;
import java.util.Map;

@Component
public class EmbeddingModelBuildHelper {

    @Autowired
    ModelsMapper modelsMapper;

    private final Map<String, String> modelConfig = new HashMap<>();

    /**
     * 构建向量模型
     * @param datasetInfo KnowledgeDatasetEntity
     * @return EmbeddingModel
     */
    public EmbeddingModel build(KnowledgeDatasetEntity datasetInfo) {
        // 默认内存型的模型
        if (datasetInfo.getEmbeddingModel().equals("AllMiniLmL6V2Embedding")) {

            return new AllMiniLmL6V2EmbeddingModel();
        }

        // 查询模型信息
        ModelsEntity modelInfo = modelsMapper.selectById(datasetInfo.getEmbeddingModelId());

        JSONArray jsonConfig = JSONUtil.parseArray(modelInfo.getCredential());
        modelConfig.put("key", jsonConfig.getJSONObject(0).getStr("value"));
        modelConfig.put("model", datasetInfo.getEmbeddingModel());

        JSONArray jsonOptions = JSONUtil.parseArray(modelInfo.getOptions());
        if (jsonOptions.size() == 2) {
            String url = jsonOptions.getJSONObject(1).getStr("value");
            modelConfig.put("baseUrl", url);
        }

        // 清华智普
        if (modelInfo.getModelFlag().equals("zhipu")) {
            return buildZhiPu();
        }

        // 百度千帆、GPT
        return buildOpenAI();
    }

    /**
     * 构建智普
     * @return EmbeddingModel
     */
    private EmbeddingModel buildZhiPu() {

        return ZhipuAiEmbeddingModel.builder()
                .model(modelConfig.get("model"))
                .apiKey(modelConfig.get("key"))
                .maxRetries(1)
                .build();
    }

    /**
     * 通过标准openai结构构建对象
     * @return EmbeddingModel
     */
    private EmbeddingModel buildOpenAI() {

        return OpenAiEmbeddingModel.builder()
                .baseUrl(modelConfig.get("baseUrl"))
                .apiKey(modelConfig.get("key"))
                .modelName(modelConfig.get("model"))
                .build();
    }
}