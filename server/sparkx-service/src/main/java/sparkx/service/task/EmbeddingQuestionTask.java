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
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import sparkx.common.enums.SourceType;
import sparkx.common.enums.StatusEnum;
import sparkx.common.utils.Tool;
import sparkx.common.utils.TsVectorGenerator;
import sparkx.service.entity.dataset.KnowledgeDatasetEntity;
import sparkx.service.entity.dataset.KnowledgeEmbeddingEntity;
import sparkx.service.entity.dataset.KnowledgeQuestionEntity;
import sparkx.service.helper.ApplicationHelper;
import sparkx.service.helper.EmbeddingModelBuildHelper;
import sparkx.service.mapper.dataset.KnowledgeEmbeddingMapper;
import sparkx.service.mapper.dataset.KnowledgeQuestionMapper;
import sparkx.service.vo.question.QuestionRelationVo;

import java.util.List;

@Component
public class EmbeddingQuestionTask {

    @Autowired
    KnowledgeQuestionMapper knowledgeQuestionMapper;

    @Autowired
    KnowledgeEmbeddingMapper knowledgeEmbeddingMapper;

    @Autowired
    EmbeddingModelBuildHelper embeddingModelBuildHelper;

    @Autowired
    ApplicationHelper applicationHelper;

    /**
     * 向量化 问题-段落
     * @param questionId String 问题id
     * @param relationVo QuestionRelationVo
     * @param datasetInfo KnowledgeDatasetEntity
     */
    @Async
    public void executeAsyncTask(String questionId, QuestionRelationVo relationVo, KnowledgeDatasetEntity datasetInfo) {

        // 选择embedding模型
        EmbeddingModel embeddingModel = embeddingModelBuildHelper.build(datasetInfo);

        KnowledgeQuestionEntity questionInfo = knowledgeQuestionMapper.selectById(questionId);
        if (questionInfo != null) {

            // 删除旧的关联
            knowledgeEmbeddingMapper.delete(new QueryWrapper<KnowledgeEmbeddingEntity>()
                    .eq("source_type", SourceType.QUESTION.getCode())
                    .eq("source_id", questionId)
                    .eq("paragraph_id", relationVo.getParagraphId()));

            // 开始向量化，并入库
            KnowledgeEmbeddingEntity embeddingEntity = new KnowledgeEmbeddingEntity();
            embeddingEntity.setEmbeddingId(IdUtil.randomUUID());
            embeddingEntity.setDatasetId(questionInfo.getDatasetId());
            embeddingEntity.setDocumentId(relationVo.getDocumentId());
            embeddingEntity.setParagraphId(relationVo.getParagraphId());

            Response<Embedding> response = embeddingModel.embed(questionInfo.getContent());
            List<Float> vector = response.content().vectorAsList();
            embeddingEntity.setEmbedding(vector); // 向量化文本
            // 记录token消耗记录
            applicationHelper.writeEmbeddingTokensLog(datasetInfo, response, "embedding");

            embeddingEntity.setSearchVector(TsVectorGenerator.toTsVector(questionInfo.getContent())); // 全文检索文本
            embeddingEntity.setActive(StatusEnum.YES.getCode());
            embeddingEntity.setSourceType(SourceType.QUESTION.getCode()); // 来源问题
            embeddingEntity.setSourceId(questionId); // 来源id
            embeddingEntity.setCreateTime(Tool.nowDateTime());

            knowledgeEmbeddingMapper.insert(embeddingEntity);
        }
    }
}
