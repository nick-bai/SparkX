// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.service.task;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import sparkx.common.enums.DocumentStatusEnum;
import sparkx.common.enums.SourceType;
import sparkx.common.enums.StatusEnum;
import sparkx.common.utils.MarkChunk;
import sparkx.common.utils.Tool;
import sparkx.common.utils.TsVectorGenerator;
import sparkx.service.entity.application.ApplicationEntity;
import sparkx.service.entity.dataset.*;
import sparkx.service.entity.system.ModelsEntity;
import sparkx.service.helper.ApplicationHelper;
import sparkx.service.helper.ChatModelBuildHelper;
import sparkx.service.helper.EmbeddingModelBuildHelper;
import sparkx.service.mapper.dataset.*;
import sparkx.service.vo.document.QuestionVo;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@Service
public class EmbeddingDocumentTask {

    @Autowired
    KnowledgeParagraphMapper knowledgeParagraphMapper;

    @Autowired
    KnowledgeDocumentMapper knowledgeDocumentMapper;

    @Autowired
    KnowledgeEmbeddingMapper knowledgeEmbeddingMapper;

    @Autowired
    KnowledgeQuestionParagraphMapper knowledgeQuestionParagraphMapper;

    @Autowired
    private KnowledgeQuestionMapper knowledgeQuestionMapper;

    @Autowired
    MarkChunk markChunk;

    @Autowired
    ChatModelBuildHelper chatModelBuildHelper;

    @Autowired
    EmbeddingModelBuildHelper embeddingModelBuildHelper;

    @Autowired
    KnowledgeDatasetMapper knowledgeDatasetMapper;

    @Autowired
    ApplicationHelper applicationHelper;

    private EmbeddingModel embeddingModel;

    /**
     * 向量化文本
     * @param documentId String
     * @param datasetInfo KnowledgeDatasetEntity
     */
    @Async
    public void executeAsyncTask(String documentId, KnowledgeDatasetEntity datasetInfo) {

        // 查询文档所属的段落
        List<KnowledgeParagraphEntity> paragraphEntityList = knowledgeParagraphMapper.selectList(
                new QueryWrapper<KnowledgeParagraphEntity>()
                .eq("document_id", documentId).eq("active", StatusEnum.YES.getCode()));

        if (!CollectionUtils.isEmpty(paragraphEntityList)) {
            // 删除已经向量化的数据
            knowledgeEmbeddingMapper.delete(new QueryWrapper<KnowledgeEmbeddingEntity>().eq("document_id", documentId));

            // 选择embedding模型
            embeddingModel = embeddingModelBuildHelper.build(datasetInfo);

            for (KnowledgeParagraphEntity paragraph : paragraphEntityList) {
                this.embeddingSingleParagraph(paragraph);
            }

            // 标记向量化完成
            KnowledgeDocumentEntity finalUpdateEntity = knowledgeDocumentMapper.selectById(documentId);
            finalUpdateEntity.setStatus(DocumentStatusEnum.COMPLETE.getCode());
            finalUpdateEntity.setEmbeddingTime(Tool.nowDateTime());
            finalUpdateEntity.setUpdateTime(Tool.nowDateTime());
            knowledgeDocumentMapper.updateById(finalUpdateEntity);
        }
    }

    /**
     * 向量化单个段落
     * @param paragraphId String
     */
    @Async
    public void executeAsyncParagraphTask(String paragraphId) {

        KnowledgeParagraphEntity paragraphInfo = knowledgeParagraphMapper.selectById(paragraphId);
        // 删除已经向量化的数据
        knowledgeEmbeddingMapper.delete(new QueryWrapper<KnowledgeEmbeddingEntity>().eq("paragraph_id", paragraphId));

        // 选择embedding模型
        KnowledgeDatasetEntity datasetInfo = knowledgeDatasetMapper.selectById(paragraphInfo.getDatasetId());
        embeddingModel = embeddingModelBuildHelper.build(datasetInfo);

        this.embeddingSingleParagraph(paragraphInfo);
    }

    /**
     * 异步执行段落生成问题
     * @param modelSetData List<String>
     * @param modelInfo ModelsEntity
     * @param documentIds List<String>
     * @param questionVo QuestionVo
     */
    @Async
    public void executeAsyncQuestionTask(List<String> modelSetData, ModelsEntity modelInfo, List<String> documentIds, QuestionVo questionVo) {

        // 构建模型普通对象
        ApplicationEntity applicationInfo = new ApplicationEntity();
        applicationInfo.setTemperature(0.95);
        applicationInfo.setModelName(modelSetData.get(1));
        ChatModel chatModel = chatModelBuildHelper.build(modelInfo, applicationInfo);

        for (String documentId : documentIds) {

            KnowledgeDocumentEntity documentInfo = knowledgeDocumentMapper.selectById(documentId);
            documentInfo.setQuestionStatus(2); // 生成中
            knowledgeDocumentMapper.updateById(documentInfo);

            // 查出分段内容
            List<KnowledgeParagraphEntity> paragraphList = knowledgeParagraphMapper.selectList(
                    new QueryWrapper<KnowledgeParagraphEntity>().eq("document_id", documentId).eq("status", 1));

            for (KnowledgeParagraphEntity paragraph : paragraphList) {

                String question = questionVo.getPrompt().replace("{data}", paragraph.getContent());
                ChatResponse chatResponse = chatModel.chat(UserMessage.from(question));

                // 记录token使用情况
                applicationHelper.writeTokenLog("question", modelInfo.getName(), chatResponse.tokenUsage());

                Document doc = Jsoup.parse(chatResponse.aiMessage().text());
                Elements questions = doc.select("question");

                for (Element questionMatch : questions) {

                    // 写入问题
                    KnowledgeQuestionEntity questionEntity = new KnowledgeQuestionEntity();
                    questionEntity.setQuestionId(IdUtil.randomUUID());
                    questionEntity.setContent(questionMatch.text());
                    questionEntity.setHitNums(0);
                    questionEntity.setDatasetId(paragraph.getDatasetId());
                    questionEntity.setCreateTime(Tool.nowDateTime());
                    knowledgeQuestionMapper.insert(questionEntity);

                    // 写入问题关联
                    KnowledgeQuestionParagraphEntity questionParagraph = new KnowledgeQuestionParagraphEntity();
                    questionParagraph.setUuid(IdUtil.randomUUID());
                    questionParagraph.setDatasetId(paragraph.getDatasetId());
                    questionParagraph.setDocumentId(paragraph.getDocumentId());
                    questionParagraph.setParagraphId(paragraph.getParagraphId());
                    questionParagraph.setQuestionId(questionEntity.getQuestionId());
                    questionParagraph.setCreateTime(Tool.nowDateTime());
                    knowledgeQuestionParagraphMapper.insert(questionParagraph);
                }
            }

            documentInfo.setQuestionStatus(3); // 已生成
            knowledgeDocumentMapper.updateById(documentInfo);
        }
    }

    /**
     * 处理数据向量化
     * @param paragraph KnowledgeParagraphEntity
     */
    private void embeddingSingleParagraph(KnowledgeParagraphEntity paragraph) {

        // 拆分段落长度，防止截取256的长度，去进行向量化，有一些embedding模型要求的最大上下文是256
        String paragraphStr = paragraph.getTitle() + paragraph.getContent();
        List<String> subParagraph = new LinkedList<>();
        if (paragraphStr.length() > 256) {
            subParagraph = markChunk.handle(paragraphStr);
        } else {
            subParagraph.add(paragraphStr);
        }

        KnowledgeDatasetEntity datasetInfo = knowledgeDatasetMapper.selectById(paragraph.getDatasetId());

        for (String content : subParagraph) {

            // 开始向量化，并入库
            KnowledgeEmbeddingEntity embeddingEntity = new KnowledgeEmbeddingEntity();
            embeddingEntity.setEmbeddingId(IdUtil.randomUUID());
            embeddingEntity.setDatasetId(paragraph.getDatasetId());
            embeddingEntity.setDocumentId(paragraph.getDocumentId());
            embeddingEntity.setParagraphId(paragraph.getParagraphId());

            Response<Embedding> response = embeddingModel.embed(content);
            List<Float> vector = response.content().vectorAsList();
            embeddingEntity.setEmbedding(vector); // 向量化文本
            // 记录token消耗记录
            applicationHelper.writeEmbeddingTokensLog(datasetInfo, response, "embedding");

            embeddingEntity.setSearchVector(TsVectorGenerator.toTsVector(content)); // 全文检索文本
            embeddingEntity.setActive(StatusEnum.YES.getCode());
            embeddingEntity.setSourceType(SourceType.DOCUMENT.getCode()); // 来源文本
            embeddingEntity.setSourceId(paragraph.getParagraphId()); // 来源id
            embeddingEntity.setCreateTime(Tool.nowDateTime());

            knowledgeEmbeddingMapper.insert(embeddingEntity);
        }

        // 段落关联的问题，也得重新索引
        List<KnowledgeQuestionParagraphEntity> relationList = knowledgeQuestionParagraphMapper.selectList(
                new QueryWrapper<KnowledgeQuestionParagraphEntity>()
                        .eq("paragraph_id", paragraph.getParagraphId()));

        // 查出段落问题信息
        List<String> questionIds = relationList.stream().map(KnowledgeQuestionParagraphEntity::getQuestionId).toList();
        if (!questionIds.isEmpty()) {

            List<KnowledgeQuestionEntity> questionList = knowledgeQuestionMapper.selectByIds(questionIds);

            HashMap<String, KnowledgeQuestionEntity> questionId2Info = new HashMap<>();
            for (KnowledgeQuestionEntity question : questionList) {
                questionId2Info.put(question.getQuestionId(), question);
            }

            for (KnowledgeQuestionParagraphEntity relation : relationList) {

                // 开始向量化，并入库
                KnowledgeEmbeddingEntity embeddingEntity = new KnowledgeEmbeddingEntity();
                embeddingEntity.setEmbeddingId(IdUtil.randomUUID());
                embeddingEntity.setDatasetId(relation.getDatasetId());
                embeddingEntity.setDocumentId(relation.getDocumentId());
                embeddingEntity.setParagraphId(relation.getParagraphId());

                String content = questionId2Info.get(relation.getQuestionId()).getContent();
                Response<Embedding> response = embeddingModel.embed(content);
                List<Float> vector = response.content().vectorAsList();
                embeddingEntity.setEmbedding(vector); // 向量化文本
                // 记录token消耗记录
                applicationHelper.writeEmbeddingTokensLog(datasetInfo, response, "embedding");

                embeddingEntity.setSearchVector(TsVectorGenerator.toTsVector(content)); // 全文检索文本
                embeddingEntity.setActive(StatusEnum.YES.getCode());
                embeddingEntity.setSourceType(SourceType.QUESTION.getCode()); // 来源问题
                embeddingEntity.setSourceId(relation.getQuestionId()); // 来源id
                embeddingEntity.setCreateTime(Tool.nowDateTime());

                knowledgeEmbeddingMapper.insert(embeddingEntity);
            }
        }
    }
}