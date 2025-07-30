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
import cn.hutool.jwt.JWT;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import dev.langchain4j.model.chat.listener.ChatModelRequestContext;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ChatRequestParameters;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.output.TokenUsage;
import dev.langchain4j.model.scoring.ScoringModel;
import dev.langchain4j.rag.content.aggregator.ContentAggregator;
import dev.langchain4j.rag.content.aggregator.ReRankingContentAggregator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import sparkx.common.constant.SparkXConstant;
import sparkx.common.utils.Tool;
import sparkx.service.entity.application.ApplicationDatasetRelationEntity;
import sparkx.service.entity.application.ApplicationToolRelationEntity;
import sparkx.service.entity.dataset.KnowledgeDatasetEntity;
import sparkx.service.entity.system.ModelsEntity;
import sparkx.service.entity.system.SystemTokensEntity;
import sparkx.service.entity.tool.ToolsEntity;
import sparkx.service.entity.workflow.ApplicationWorkflowRuntimeContextEntity;
import sparkx.service.extend.rerank.RerankScoringModel;
import sparkx.service.mapper.application.ApplicationDatasetRelationMapper;
import sparkx.service.mapper.application.ApplicationToolRelationMapper;
import sparkx.service.mapper.application.ApplicationWorkflowRuntimeContextMapper;
import sparkx.service.mapper.dataset.KnowledgeDatasetMapper;
import sparkx.service.mapper.system.ModelsMapper;
import sparkx.service.mapper.system.SystemTokensMapper;
import sparkx.service.mapper.tool.ToolsMapper;
import sparkx.service.vo.dataset.DatasetSimpleVo;
import sparkx.service.vo.system.LocalUserVo;
import sparkx.service.vo.tool.ToolsSimpleListVo;
import sparkx.service.vo.workflow.EdgeVo;
import sparkx.service.vo.workflow.NextAnswerNodeVo;
import sparkx.service.vo.workflow.NodeRuntimeVo;
import sparkx.service.vo.workflow.NodeVo;

import java.util.AbstractMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Component
@Slf4j
public class ApplicationHelper {

    @Autowired
    ApplicationDatasetRelationMapper applicationDatasetRelationMapper;

    @Autowired
    ApplicationToolRelationMapper applicationToolRelationMapper;

    @Autowired
    ApplicationWorkflowRuntimeContextMapper applicationWorkflowRuntimeContextMapper;

    @Autowired
    KnowledgeDatasetMapper knowledgeDatasetMapper;

    @Autowired
    SystemTokensMapper systemTokensMapper;

    @Autowired
    ModelsMapper modelsMapper;

    @Autowired
    ToolsMapper toolsMapper;

    /**
     * 获取关联的知识库
     * @param appId String
     * @return List<DatasetSimpleVo>
     */
    public List<DatasetSimpleVo> getRelationDatasetList(String appId) {

        List<DatasetSimpleVo> datasetSimpleVoList = new LinkedList<>();

        List<ApplicationDatasetRelationEntity> relationEntityList =
                applicationDatasetRelationMapper.selectList(new QueryWrapper<ApplicationDatasetRelationEntity>()
                        .eq("app_id", appId));
        if (!CollectionUtils.isEmpty(relationEntityList)) {

            List<String> datasetIds = relationEntityList.stream().map(ApplicationDatasetRelationEntity::getDatasetId).toList();
            List<KnowledgeDatasetEntity> datasetList = knowledgeDatasetMapper.selectByIds(datasetIds);
            for (KnowledgeDatasetEntity entity : datasetList) {

                DatasetSimpleVo vo = new DatasetSimpleVo();
                BeanUtils.copyProperties(entity, vo);

                datasetSimpleVoList.add(vo);
            }
        }

        return datasetSimpleVoList;
    }

    /**
     * 获取关联的插件
     * @param appId String
     * @return List<ToolsSimpleListVo>
     */
    public List<ToolsSimpleListVo> getRelationToolList(String appId) {

        List<ToolsSimpleListVo> toolsSimpleListVo = new LinkedList<>();

        List<ApplicationToolRelationEntity> relationEntityList =
                applicationToolRelationMapper.selectList(new QueryWrapper<ApplicationToolRelationEntity>()
                        .eq("app_id", appId));
        if (!CollectionUtils.isEmpty(relationEntityList)) {

            List<Integer> toolIds = relationEntityList.stream().map(ApplicationToolRelationEntity::getToolId).toList();
            List<ToolsEntity> toolsList = toolsMapper.selectByIds(toolIds);
            for (ToolsEntity entity : toolsList) {

                ToolsSimpleListVo vo = new ToolsSimpleListVo();
                BeanUtils.copyProperties(entity, vo);

                toolsSimpleListVo.add(vo);
            }
        }

        return toolsSimpleListVo;
    }

    /**
     * 查询关联的插件
     * @param appId String
     * @return List<ToolsEntity>
     */
    public List<ToolsEntity> getRelationFullToolList(String appId) {

        List<ApplicationToolRelationEntity> relationEntityList =
                applicationToolRelationMapper.selectList(new QueryWrapper<ApplicationToolRelationEntity>()
                        .eq("app_id", appId));
        if (!CollectionUtils.isEmpty(relationEntityList)) {

            List<Integer> toolIds = relationEntityList.stream().map(ApplicationToolRelationEntity::getToolId).toList();
            return toolsMapper.selectByIds(toolIds);
        }

        return null;
    }

    /**
     * 获取运行时上下文节点
     * @param runtimeId long
     * @param sourceId String
     * @param inputSourceId String
     * @return ApplicationWorkflowRuntimeContextEntity
     */
    public ApplicationWorkflowRuntimeContextEntity getRuntimeContext(long runtimeId, String sourceId, String inputSourceId) {

        ApplicationWorkflowRuntimeContextEntity returnContext = null;

        List<ApplicationWorkflowRuntimeContextEntity> context = applicationWorkflowRuntimeContextMapper.selectList(
                new QueryWrapper<ApplicationWorkflowRuntimeContextEntity>().eq("runtime_id", runtimeId).eq("cell", sourceId));

        if (context == null || context.isEmpty()) {
            return null;
        }

        // 如果上级节点只有一个，不存在并行节点，则直接返回
        if (context.size() == 1) {
            return context.get(0);
        }

        // 如果存在并行节点，则判断当前节点的输入是否是上个节点的输出，如果你是，则返回选定的上个节点，
        for (ApplicationWorkflowRuntimeContextEntity contextItem : context) {
            if (contextItem.getCell().equals(inputSourceId)) {
                returnContext = contextItem;
            }
        }

        // 如果不是，则直接返回任意一个上游节点，这里选第一个，因为上下文输入中保存了全部上游节点的输出
        if (returnContext == null) {
            return context.get(0);
        }

        return returnContext;
    }

    /**
     * 检测下个节点是否为回复节点以及回复节点的回复类型
     * @param runtimeVo NodeRuntimeVo
     * @return NextAnswerNodeVo
     */
    public NextAnswerNodeVo checkNextIsAnswerNode(NodeRuntimeVo runtimeVo) {

        NextAnswerNodeVo nextAnswerNodeVo = new NextAnswerNodeVo();
        List<EdgeVo> nextEdgeVoList = runtimeVo.getEdges().get(runtimeVo.getNodeInfo().getId());
        if (CollectionUtils.isEmpty(nextEdgeVoList)) {
            nextAnswerNodeVo.setNodeIsAnswer(false);
            nextAnswerNodeVo.setAnswerType(2);

            return nextAnswerNodeVo;
        }

        Map<String, NodeVo> nodes = runtimeVo.getNodes();
        AtomicBoolean hasAnswerNode = new AtomicBoolean(false);
        AtomicInteger answerType = new AtomicInteger(2);
        NodeVo nowNodeInfo = runtimeVo.getNodeInfo();

        for (EdgeVo edgeItem : nextEdgeVoList) {

            List<String> targetIds = edgeItem.getTarget();
            for (String targetId : targetIds) {
                NodeVo nodeInfo = nodes.get(targetId);

                if (nodeInfo.getShape().equals("answer-node")) {

                    // 检测下个节点的输入是否是当前节点
                    JSONArray inputArr = nodeInfo.getData().getJSONArray("inputData");
                    String inputNodeId = inputArr.get(0).toString();
                    if (nowNodeInfo.getId().equals(inputNodeId)) {
                        answerType.set(1);
                    }

                    hasAnswerNode.set(true);
                }
            }
        }

        nextAnswerNodeVo.setNodeIsAnswer(hasAnswerNode.get());
        nextAnswerNodeVo.setAnswerType(answerType.get());

        return nextAnswerNodeVo;
    }

    /**
     * 获取token中的用户
     * @return LocalUserVo
     */
    public LocalUserVo getUserData() {

        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();

        // 令牌验证
        String token = request.getHeader("Authorization");
        LocalUserVo localUser = new LocalUserVo();
        try {

            token = token.replace("Bearer ", "");
            boolean validate = JWT.of(token).setKey(SparkXConstant.CommonData.passwordSalt.getBytes()).validate(0);
            if (!validate) {
                throw new Exception();
            }

            JWT jwt = JWT.of(token);
            localUser.setUserId(String.valueOf(jwt.getPayload("userId")));
        } catch (Exception e) {
            return localUser;
        }

        return localUser;
    }

    /**
     * 记录token使用日志
     * @param datasetInfo KnowledgeDatasetEntity
     * @param response Response<Embedding>
     * @param type String
     */
    public void writeEmbeddingTokensLog(KnowledgeDatasetEntity datasetInfo, Response<Embedding> response, String type) {

        if (!datasetInfo.getEmbeddingModel().equals("AllMiniLmL6V2Embedding")) {

            TokenUsage tokenUsage = response.tokenUsage();
            // Ollama的一些本地模型，没有返回使用的token
            if (tokenUsage != null) {
                ModelsEntity modelInfo = modelsMapper.selectById(datasetInfo.getEmbeddingModelId());
                SystemTokensEntity tokensEntity = new SystemTokensEntity();
                tokensEntity.setSource(type);
                tokensEntity.setPlatform(modelInfo.getName());
                tokensEntity.setInputToken(tokenUsage.inputTokenCount());
                tokensEntity.setOutputToken(tokenUsage.outputTokenCount());
                tokensEntity.setTotalToken(tokenUsage.totalTokenCount());
                tokensEntity.setCreateTime(Tool.nowDateTime());
                systemTokensMapper.insert(tokensEntity);
            }
        }
    }

    /**
     * 记录token日志
     * @param source String
     * @param platform String
     * @param tokenUsage TokenUsage
     */
    public void writeTokenLog(String source, String platform, TokenUsage tokenUsage) {

        SystemTokensEntity tokensEntity = new SystemTokensEntity();
        tokensEntity.setSource(source);
        tokensEntity.setPlatform(platform);
        tokensEntity.setInputToken(tokenUsage.inputTokenCount());
        tokensEntity.setOutputToken(tokenUsage.outputTokenCount());
        tokensEntity.setTotalToken(tokenUsage.totalTokenCount());
        tokensEntity.setCreateTime(Tool.nowDateTime());
        systemTokensMapper.insert(tokensEntity);
    }

    /**
     * 构建调用可见性
     * @return ChatModelListener
     */
    public ChatModelListener observability() {

        return new ChatModelListener() {

            @Override
            public void onRequest(ChatModelRequestContext requestContext) {

                ChatRequest chatRequest = requestContext.chatRequest();
                List<ChatMessage> messages = chatRequest.messages();
                log.error("调用的消息: message {}", messages);
                ChatRequestParameters parameters = chatRequest.parameters();
                log.error("调用的参数: parameters {}", parameters);
            }
        };
    }

    /**
     * 构建rerank模型
     * @param modelId String
     * @return ContentAggregator
     */
    public ContentAggregator buildRerank(String modelId) {

        ContentAggregator contentAggregator = null;
        ModelsEntity modelInfo = modelsMapper.selectById(modelId);
        if (modelInfo != null) {
            JSONArray jsonArr = JSONUtil.parseArray(modelInfo.getCredential());
            String apiKey = JSONUtil.parseObj(jsonArr.get(0)).getStr("value");
            String modelName = modelInfo.getModels().split(",")[0];

            JSONArray optionsArr = JSONUtil.parseArray(modelInfo.getOptions());
            String baseUrl = JSONUtil.parseObj(optionsArr.get(0)).getStr("value");

            if (!apiKey.isBlank() && !modelName.isBlank() && !baseUrl.isBlank()) {

                // 构建重排模型
                ScoringModel scoringModel = RerankScoringModel.builder()
                        .apiKey(apiKey)
                        .baseUrl(baseUrl)
                        .modelName(modelName)
                        .build();

                contentAggregator = ReRankingContentAggregator.builder()
                        .scoringModel(scoringModel)
                        .build();
            }
        }

        return contentAggregator;
    }

    /**
     * 重排结果
     * @param modelId String
     * @param result List<String>
     * @param query String
     * @return List<String>
     */
    public List<String> rerankResult(String modelId, List<String> result, String query) {

        if (result.size() <= 1) {
            return null;
        }

        ModelsEntity modelInfo = modelsMapper.selectById(modelId);
        if (modelInfo != null) {
            JSONArray jsonArr = JSONUtil.parseArray(modelInfo.getCredential());
            String apiKey = JSONUtil.parseObj(jsonArr.get(0)).getStr("value");
            String modelName = modelInfo.getModels().split(",")[0];

            JSONArray optionsArr = JSONUtil.parseArray(modelInfo.getOptions());
            String baseUrl = JSONUtil.parseObj(optionsArr.get(0)).getStr("value");

            if (!apiKey.isBlank() && !modelName.isBlank() && !baseUrl.isBlank()) {

                // 构建重排模型
                ScoringModel scoringModel = RerankScoringModel.builder()
                        .apiKey(apiKey)
                        .baseUrl(baseUrl)
                        .modelName(modelName)
                        .build();

                Response<List<Double>> scoreRes = scoringModel.scoreAll(result.stream()
                        .map(TextSegment::from).collect(Collectors.toList()), query);
                List<Double> scoreList = scoreRes.content();

                // 根据重排结果重排原始数组
                return IntStream.range(0, result.size())
                        .mapToObj(i -> new AbstractMap.SimpleEntry<>(result.get(i), scoreList.get(i)))
                        .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                        .map(AbstractMap.SimpleEntry::getKey)
                        .collect(Collectors.toList());
            }
        }

        return null;
    }
}