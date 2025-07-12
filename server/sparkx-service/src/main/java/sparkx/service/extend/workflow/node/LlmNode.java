// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.service.extend.workflow.node;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import sparkx.common.enums.NodeTypeEnum;
import sparkx.common.exception.BusinessException;
import sparkx.common.utils.Tool;
import sparkx.service.entity.application.ApplicationEntity;
import sparkx.service.entity.system.ModelsEntity;
import sparkx.service.entity.workflow.ApplicationWorkflowRuntimeContextEntity;
import sparkx.service.extend.workflow.IWorkflowNode;
import sparkx.service.helper.*;
import sparkx.service.mapper.application.ApplicationWorkflowRuntimeContextMapper;
import sparkx.service.mapper.system.ModelsMapper;
import sparkx.service.service.interfaces.application.IAiService;
import sparkx.service.validate.application.ApplicationChatValidate;
import sparkx.service.vo.workflow.EdgeVo;
import sparkx.service.vo.workflow.LlmAnswerVo;
import sparkx.service.vo.workflow.NextAnswerNodeVo;
import sparkx.service.vo.workflow.NodeRuntimeVo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static dev.langchain4j.data.message.ChatMessageSerializer.messagesToJson;

@Slf4j
@Component
public class LlmNode implements IWorkflowNode {

    @Setter
    public SseEmitter emitter;

    @Setter
    public CountDownLatch latch;

    @Autowired
    ApplicationWorkflowRuntimeContextMapper applicationWorkflowRuntimeContextMapper;

    @Autowired
    ApplicationHelper applicationHelper;

    @Autowired
    ModelsMapper modelsMapper;

    @Autowired
    StreamChatModelBuildHelper streamChatModelBuildHelper;

    @Autowired
    ChatModelBuildHelper chatModelBuildHelper;

    @Autowired
    SseEmitterHelper sseEmitterHelper;

    @Autowired
    MemoryBuildHelper memoryBuildHelper;

    @Override
    public List<EdgeVo> handle(NodeRuntimeVo runtimeVo) {

        JSONObject nodeObject = runtimeVo.getNodeInfo().getData();
        // 本节点输入的参数
        String inputSourceId = "";

        // 获取上一个节点的信息
        ApplicationWorkflowRuntimeContextEntity context =
                applicationHelper.getRuntimeContext(runtimeVo.getRuntimeId(), runtimeVo.getSourceId(), inputSourceId);
        if (context == null) {
            return null;
        }

        JSONObject preOutput = JSONUtil.parseObj(context.getOutputData());

        // 记录运行时数据
        ApplicationWorkflowRuntimeContextEntity contextEntity = new ApplicationWorkflowRuntimeContextEntity();
        contextEntity.setStep(context.getStep() + 1);
        contextEntity.setNodeType(NodeTypeEnum.LLM.getCode());
        contextEntity.setRuntimeId(runtimeVo.getRuntimeId());
        contextEntity.setModelData(nodeObject.toString());
        contextEntity.setOutputData(preOutput.toString());
        contextEntity.setCell(runtimeVo.getNodeInfo().getId());
        contextEntity.setCreateTime(Tool.nowDateTime());
        applicationWorkflowRuntimeContextMapper.insert(contextEntity);

        // 查看下一个节点是否是回复节点，且回复的内容是本节点的输出
        NextAnswerNodeVo nextAnswerNodeInfo = applicationHelper.checkNextIsAnswerNode(runtimeVo);
        boolean needSend = (nextAnswerNodeInfo.isNodeIsAnswer() && nextAnswerNodeInfo.getAnswerType() == 1);

        try {

            // 本节点的输出信息
            TokenStream tokenStream = llmAnswer(contextEntity, runtimeVo);
            if (tokenStream == null) {
                throw new BusinessException("LLM节点出现系统异常");
            }

            AtomicBoolean runComplete = new AtomicBoolean(false);
            sseEmitterHelper.asyncSend2Client(tokenStream, emitter, contextEntity.getRuntimeId(),
                    contextEntity.getCell(), needSend, (llmRes) -> {

                        JSONObject llmResData = JSONUtil.parseObj(llmRes);
                        String answer = llmResData.getStr("content");

                        // 记录llm输出
                        JSONObject preContextOutput = JSONUtil.parseObj(contextEntity.getOutputData());
                        preContextOutput.set("sys.content", answer);
                        contextEntity.setOutputData(preContextOutput.toString());

                        // token使用情况
                        JSONObject modelData = JSONUtil.createObj();
                        modelData.set("inputTokenCount", llmResData.getStr("inputTokenCount"));
                        modelData.set("outputTokenCount", llmResData.getStr("outputTokenCount"));
                        modelData.set("totalTokenCount", llmResData.getStr("totalTokenCount"));
                        contextEntity.setModelData(modelData.toString());

                        applicationWorkflowRuntimeContextMapper.updateById(contextEntity);

                        runComplete.set(true);
                    });

            // 阻塞等待异步发送完成
            while (!runComplete.get()) {}

        } catch (Exception e) {
            log.error("知识库检索节点错误, {}", e.getMessage());
            throw new BusinessException("知识库检索节点错误");
        }

        latch.countDown();

        // 获取下一个节点
        return runtimeVo.getEdges().get(runtimeVo.getNodeInfo().getId());
    }

    /**
     * 大模型流式回答
     * @param context ApplicationWorkflowRuntimeContextEntity
     * @param runtimeVo NodeRuntimeVo
     * @return String
     */
    private TokenStream llmAnswer(ApplicationWorkflowRuntimeContextEntity context, NodeRuntimeVo runtimeVo) {

        try {

            LlmAnswerVo llmAnswerData = buildBaseData(context, runtimeVo);

            TokenStream tokenStream;
            if (llmAnswerData.getApplication().getPrompt().isBlank()) {
                tokenStream = llmAnswerData.getAssistant().chatInTokenStream(llmAnswerData.getUserMessage());
            } else {
                tokenStream = llmAnswerData.getAssistant().chatWithSystem(llmAnswerData.getApplication().getPrompt(), llmAnswerData.getUserMessage());
            }

            return tokenStream;
        } catch (Exception e) {
            log.error("回复节点构建llm错误：", e);
            return null;
        }
    }

    /**
     * 构建基础信息
     * @param context ApplicationWorkflowRuntimeContextEntity
     * @param runtimeVo NodeRuntimeVo
     * @return LlmAnswerVo
     */
    private LlmAnswerVo buildBaseData(ApplicationWorkflowRuntimeContextEntity context, NodeRuntimeVo runtimeVo) {

        LlmAnswerVo llmAnswerVo = new LlmAnswerVo();

        String userId = runtimeVo.getUserId();
        String sessionId = runtimeVo.getSessionId();
        JSONObject inputObject = JSONUtil.parseObj(context.getOutputData());
        JSONObject modelObject = JSONUtil.parseObj(context.getModelData());

        JSONObject modelDataInfo = modelObject.getJSONObject("modelInfo");
        // 获取模型信息
        ModelsEntity modelResInfo = modelsMapper.selectById(modelDataInfo.getStr("modelId"));

        // step 1 构建模型流式应答对象
        ApplicationEntity applicationInfo = new ApplicationEntity();
        applicationInfo.setTemperature(modelDataInfo.getDouble("temperature"));
        applicationInfo.setModelName(modelDataInfo.getStr("modelName"));
        applicationInfo.setPrompt(modelObject.getStr("systemMsg"));
        applicationInfo.setUserId(userId);
        StreamingChatModel streamingChatModel = streamChatModelBuildHelper.build(modelResInfo, applicationInfo);

        // step 2 构建模型普通对象，用于问题优化下使用
        ChatModel chatModel = chatModelBuildHelper.build(modelResInfo, applicationInfo);

        ApplicationChatValidate validate = new ApplicationChatValidate();

        String question = inputObject.getStr("sys.question");
        validate.setContent(question);
        validate.setContextId(context.getId());
        validate.setSessionId(sessionId);
        validate.setCell(context.getCell()); // 以次区分不同节点的上下文记录

        // step 3 构建 IAiService
        // 自定义构建上下文记忆
        String memoryKey = validate.getSessionId() + applicationInfo.getUserId() + validate.getCell();
        ChatMemoryProvider chatMemoryProvider = memoryId -> MessageWindowChatMemory.builder()
                .id(memoryKey)
                .maxMessages(modelObject.getInt("memory"))
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

        // 用户未定义用户提示词，则根据是否关联了知识库来构建用户提示词
        // 用户定了提示词，则根据用户的定义去构建用户提示词
        String userPrompt = modelObject.getStr("userPrompt");
        if (userPrompt.isBlank()) {

            // 关联知识库
            if (inputObject.containsKey("node.datasets")
                    && !inputObject.getStr("node.datasets").isBlank()) {

                PromptTemplate questionPrompt = PromptTemplate.from("{{question}}\n\n Answer using the following information:\n\n {{sys.result}}");
                Map<String, Object> variables = new HashMap<>();
                variables.put("question", question);
                variables.put("sys.result", inputObject.getStr("sys.result"));

                Prompt prompt = questionPrompt.apply(variables);
                llmAnswerVo.setUserMessage(((TextContent)prompt.toUserMessage().contents().get(0)).text());
            } else { // 未关联知识库
                llmAnswerVo.setUserMessage(question);
            }
        } else {

            // 处理用户自定义提示词,替换提示词中的变量
            PromptTemplate promptTemplate = PromptTemplate.from(userPrompt);

            Pattern pattern = Pattern.compile("\\{\\{(.*?)\\}\\}");
            Matcher matcher = pattern.matcher(userPrompt);

            Map<String, Object> variables = new HashMap<>();
            while (matcher.find()) {
                variables.put(matcher.group(1), inputObject.getStr(matcher.group(1)));
            }

            Prompt prompt = promptTemplate.apply(variables);
            llmAnswerVo.setUserMessage(((TextContent)prompt.toUserMessage().contents().get(0)).text());
        }

        // 构建AIService
        IAiService assistant = AiServices.builder(IAiService.class)
                .streamingChatModel(streamingChatModel)
                .chatMemoryProvider(chatMemoryProvider) // 聊天上下文
                .build();

        llmAnswerVo.setApplication(applicationInfo);
        llmAnswerVo.setValidate(validate);
        llmAnswerVo.setStreamingChatModel(streamingChatModel);
        llmAnswerVo.setChatLanguageModel(chatModel);
        llmAnswerVo.setAssistant(assistant);

        return llmAnswerVo;
    }
}