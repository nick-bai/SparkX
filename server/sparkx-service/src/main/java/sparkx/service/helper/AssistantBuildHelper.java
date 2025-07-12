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

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.http.HttpMcpTransport;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.transformer.CompressingQueryTransformer;
import dev.langchain4j.rag.query.transformer.QueryTransformer;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolProvider;
import dev.langchain4j.service.tool.ToolProviderResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import sparkx.service.entity.application.ApplicationEntity;
import sparkx.service.entity.dataset.KnowledgeDatasetEntity;
import sparkx.service.entity.tool.ToolsEntity;
import sparkx.service.entity.workflow.ApplicationWorkflowRuntimeContextEntity;
import sparkx.service.extend.SparkEmbeddingStoreContentRetriever;
import sparkx.service.mapper.application.ApplicationWorkflowRuntimeContextMapper;
import sparkx.service.mapper.dataset.KnowledgeDatasetMapper;
import sparkx.service.service.interfaces.application.IAiService;
import sparkx.service.service.interfaces.dataset.IDatasetSearchService;
import sparkx.service.validate.application.ApplicationChatValidate;
import sparkx.service.vo.dataset.DatasetSearchVo;
import sparkx.service.vo.dataset.DatasetSimpleVo;
import sparkx.service.vo.tool.ToolParamsVo;

import java.util.List;
import java.util.Map;

import static dev.langchain4j.data.message.ChatMessageSerializer.messagesToJson;

@Component
@Slf4j
public class AssistantBuildHelper {

    @Autowired
    IDatasetSearchService iDatasetSearchService;

    @Autowired
    EmbeddingModelBuildHelper embeddingModelBuildHelper;

    @Autowired
    KnowledgeDatasetMapper knowledgeDatasetMapper;

    @Autowired
    ApplicationWorkflowRuntimeContextMapper applicationWorkflowRuntimeContextMapper;

    @Autowired
    MemoryBuildHelper memoryBuildHelper;

    /**
     * 构建 assistant
     * @param validate ApplicationSaveValidate
     * @param streamingModel StreamingChatModel
     * @param chatModel ChatModel
     * @return IAiService
     */
    public IAiService build(ApplicationEntity applicationInfo, ApplicationChatValidate validate,
                            StreamingChatModel streamingModel, ChatModel chatModel) {

        // 自定义构建上下文记忆
        String memoryKey = validate.getSessionId() + applicationInfo.getUserId() + validate.getCell();
        ChatMemoryProvider chatMemoryProvider = memoryId -> MessageWindowChatMemory.builder()
                .id(memoryKey)
                .maxMessages(applicationInfo.getMemoryNum())
                .chatMemoryStore(memoryBuildHelper)
                .build();

        // 更新上下文记忆
        if (validate.getContextId() != 0) {
            ApplicationWorkflowRuntimeContextEntity runtimeContextEntity
                    = applicationWorkflowRuntimeContextMapper.selectById(validate.getContextId());
            JSONObject outputData = JSONUtil.parseObj(runtimeContextEntity.getOutputData());
            outputData.set("log.context", messagesToJson(memoryBuildHelper.getMessages(memoryKey)));
            runtimeContextEntity.setOutputData(outputData.toString());
            applicationWorkflowRuntimeContextMapper.updateById(runtimeContextEntity);
        }

        // 构建服务
        AiServices<IAiService> builder =
                        AiServices.builder(IAiService.class)
                        .streamingChatModel(streamingModel)
                        .chatMemoryProvider(chatMemoryProvider);
        // 未关联知识库
        if (validate.getDatasetList().isEmpty()) {

            // 检测是否使用了插件
            if (!CollectionUtils.isEmpty(validate.getToolsList())) {
                return buildToolAiService(validate, streamingModel, chatMemoryProvider, null);
            }

            return builder.build();
        }

        // TODO 空召回策略

        // 关联了知识库
        QueryTransformer queryTransformer = null;
        // 开启问题优化
        if (applicationInfo.getCompressingQuery().equals(1)) {
            queryTransformer = new CompressingQueryTransformer(chatModel);
        }

        // 构建交互数据
        DatasetSearchVo searchDataVo = new DatasetSearchVo();
        searchDataVo.setType(applicationInfo.getSearchMode());
        String[] datasetIds = validate.getDatasetList().stream().map(DatasetSimpleVo::getDatasetId).toArray(String[]::new);
        searchDataVo.setDatasetIds(String.join(",", datasetIds));

        // 取第一条知识库的embedding模型当做全应用的embedding模型
        KnowledgeDatasetEntity datasetInfo = knowledgeDatasetMapper.selectById(datasetIds[0]);
        // embedding模型
        EmbeddingModel embeddingModel = embeddingModelBuildHelper.build(datasetInfo);

        // 内容检索
        ContentRetriever contentRetriever = SparkEmbeddingStoreContentRetriever.builder()
                .embeddingModel(embeddingModel)
                .searchService(iDatasetSearchService)
                .searchDataVo(searchDataVo)
                .maxResults(applicationInfo.getTopRank()) // 召回条数
                .minScore(applicationInfo.getSimilarity().doubleValue()) // 相似度
                .build();

        // 检索增强
        RetrievalAugmentor retrievalAugmentor;
        if (queryTransformer != null) {
            retrievalAugmentor = DefaultRetrievalAugmentor.builder()
                    .queryTransformer(queryTransformer) // 问题压缩
                    .contentRetriever(contentRetriever) // 内容检索
                    .build();
        } else {
            retrievalAugmentor = DefaultRetrievalAugmentor.builder()
                    .contentRetriever(contentRetriever) // 内容检索
                    .build();
        }

        // 检测是否使用了插件
        if (!CollectionUtils.isEmpty(validate.getToolsList())) {
            return buildToolAiService(validate, streamingModel, chatMemoryProvider, retrievalAugmentor);
        }

        return builder
                .retrievalAugmentor(retrievalAugmentor)
                .build();
    }

    /**
     * 构建ai调用服务
     * @param validate ApplicationChatValidate
     * @param streamingModel StreamingChatModel
     * @param chatMemoryProvider ChatMemoryProvider
     * @param retrievalAugmentor RetrievalAugmentor
     * @return IAiService
     */
    private IAiService buildToolAiService(ApplicationChatValidate validate, StreamingChatModel streamingModel,
                                          ChatMemoryProvider chatMemoryProvider, RetrievalAugmentor retrievalAugmentor) {

        // 构建插件
        ToolProvider toolProvider;
        if (validate.getToolsList().get(0).getType().equals(1)) {
            toolProvider = makeDiyToolProvider(validate);
        } else {
            toolProvider = makeMcpToolProvider(validate);
        }

        // 构建服务
        AiServices<IAiService> builder =
                AiServices.builder(IAiService.class)
                .streamingChatModel(streamingModel)
                .chatMemoryProvider(chatMemoryProvider)
                .toolProvider(toolProvider);

        if (retrievalAugmentor == null) {
            return builder.build();
        }

        return builder
                .retrievalAugmentor(retrievalAugmentor)
                .build();
    }

    /**
     * 构建自定义插件
     * @param validate ApplicationChatValidate
     * @return ToolProvider
     */
    private ToolProvider makeDiyToolProvider(ApplicationChatValidate validate) {

        return (toolProviderRequest) -> {

            ToolProviderResult.Builder builder = ToolProviderResult.builder();

            for (ToolsEntity entity : validate.getToolsList()) {

                ToolSpecification.Builder specificationBuilder = ToolSpecification.builder();
                specificationBuilder.name(entity.getName()); // 方法标识
                specificationBuilder.description(entity.getDescription()); // 方法描述
                // 构建字段
                JsonObjectSchema.Builder paramsBuilder = JsonObjectSchema.builder();
                List<ToolParamsVo> paramsList = JSONUtil.toList(entity.getPostParams(), ToolParamsVo.class);

                for (ToolParamsVo params : paramsList) {
                    if (params.getType().equals("String")) {
                        paramsBuilder.addStringProperty(params.getField(), params.getDesc());
                    } else if (params.getType().equals("Int")) {
                        paramsBuilder.addIntegerProperty(params.getField(), params.getDesc());
                    } else if (params.getType().equals("Double")) {
                        paramsBuilder.addNumberProperty(params.getField(), params.getDesc());
                    }

                    // 必填字段
                    if (params.getRequired().equals(1)) {
                        paramsBuilder.required(params.getField());
                    }
                }

                specificationBuilder.parameters(paramsBuilder.build());

                ToolSpecification toolSpecification = specificationBuilder.build();
                builder.add(toolSpecification, (toolExecutionRequest, memoryId) -> {
                    Map<String, Object> arguments = JSONUtil.parseObj(toolExecutionRequest.arguments());
                    // 请求设定的接口
                    HttpRequest httpRequest = HttpRequest.post(entity.getApiUrl());
                    if (entity.getAuthType().equals(2)) {

                        // 秘钥在header中
                        if (entity.getAuthWay().equals(1)) {
                            httpRequest.header(entity.getApiKeyName(), entity.getApiKeyValue());
                        } else { // 秘钥放在body中
                            arguments.put(entity.getApiKeyName(), entity.getApiKeyValue());
                        }
                    }

                    return httpRequest.form(arguments).execute().body();
                });
            }

            return builder.build();
        };
    }

    /**
     * 构建mcp provider
     * @param validate ApplicationChatValidate
     * @return ToolProvider
     */
    private ToolProvider makeMcpToolProvider(ApplicationChatValidate validate) {

        McpToolProvider.Builder mcpToolBuilder = McpToolProvider.builder();

        for (ToolsEntity entity : validate.getToolsList()) {

            // 构建协议
            McpTransport transport = new HttpMcpTransport.Builder()
                    .sseUrl(entity.getApiUrl())
                    .logRequests(true)
                    .logResponses(true)
                    .build();
            // 构建客户端
            McpClient mcpClient = new DefaultMcpClient.Builder()
                    .key(entity.getName())
                    .transport(transport)
                    .build();

            mcpToolBuilder.mcpClients(mcpClient);
        }

        return mcpToolBuilder.build();
    }
}
