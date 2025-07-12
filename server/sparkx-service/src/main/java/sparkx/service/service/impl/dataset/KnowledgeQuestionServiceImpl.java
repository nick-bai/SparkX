// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.service.service.impl.dataset;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import sparkx.common.core.PageResult;
import sparkx.common.enums.SourceType;
import sparkx.common.exception.BusinessException;
import sparkx.common.utils.Tool;
import sparkx.service.entity.dataset.*;
import sparkx.service.mapper.dataset.*;
import sparkx.service.service.interfaces.dataset.IKnowledgeQuestionService;
import sparkx.service.task.EmbeddingQuestionTask;
import sparkx.service.vo.question.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KnowledgeQuestionServiceImpl implements IKnowledgeQuestionService {

    @Autowired
    KnowledgeQuestionMapper knowledgeQuestionMapper;

    @Autowired
    KnowledgeQuestionParagraphMapper knowledgeQuestionParagraphMapper;

    @Autowired
    KnowledgeEmbeddingMapper knowledgeEmbeddingMapper;

    @Autowired
    EmbeddingQuestionTask questionTask;

    @Autowired
    private KnowledgeParagraphMapper knowledgeParagraphMapper;

    @Autowired
    KnowledgeDatasetMapper datasetMapper;

    @Override
    public PageResult<QuestionListVo> getQuestionList(QuestionQueryVo queryVo) {

        long pageNo   = queryVo.getPage();
        long pageSize = queryVo.getLimit();

        QueryWrapper<KnowledgeQuestionEntity> queryWrapper = new QueryWrapper<>();
        if (!queryVo.getContent().isBlank()) {
            queryWrapper.like("content", queryVo.getContent());
        }

        queryWrapper.eq("dataset_id", queryVo.getDatasetId());
        queryWrapper.orderByDesc("create_time");

        IPage<KnowledgeQuestionEntity> questionListRes =
                knowledgeQuestionMapper.selectPage(new Page<>(pageNo, pageSize), queryWrapper);

        List<QuestionListVo> questionListVoList = new LinkedList<>();
        for (KnowledgeQuestionEntity entity : questionListRes.getRecords()) {
            QuestionListVo vo = new QuestionListVo();
            BeanUtils.copyProperties(entity, vo);

            // 关联的数量
            long num = knowledgeQuestionParagraphMapper.selectCount(new QueryWrapper<KnowledgeQuestionParagraphEntity>()
                    .eq("question_id", vo.getQuestionId()));
            vo.setLinkNum(num);

            questionListVoList.add(vo);
        }

        return PageResult.iPageHandle(questionListRes.getTotal(), pageNo, pageSize, questionListVoList);
    }

    /**
     * 添加问题
     * @param questionSaveVo QuestionSaveVo
     */
    @Override
    public void addQuestion(QuestionSaveVo questionSaveVo) {

        if (questionSaveVo.getContent().isBlank()) {
            throw new BusinessException("问题不能为空");
        }

        String[] questionList = questionSaveVo.getContent().split("\n");
        // 长度检测
        for (String question : questionList) {
            if (question.length() > 255) {
                throw new BusinessException("问题长度不得超过255个字符");
            }
        }

        for (String question : questionList) {

            KnowledgeQuestionEntity questionEntity = new KnowledgeQuestionEntity();
            questionEntity.setQuestionId(IdUtil.randomUUID());
            questionEntity.setContent(question);
            questionEntity.setHitNums(0);
            questionEntity.setDatasetId(questionSaveVo.getDatasetId());
            questionEntity.setCreateTime(Tool.nowDateTime());

            knowledgeQuestionMapper.insert(questionEntity);
        }
    }

    /**
     * 获取关联信息
     * @param questionIds String
     * @param datasetId String
     * @return List<QuestionRelationListVo>
     */
    @Override
    public List<QuestionRelationListVo> getRelationList(String questionIds, String datasetId) {

        List<String> questionIdsList = Arrays.asList(questionIds.split(","));
        List<KnowledgeQuestionParagraphEntity> relationList =
                knowledgeQuestionParagraphMapper.selectList(new QueryWrapper<KnowledgeQuestionParagraphEntity>()
                                .eq("dataset_id", datasetId)
                                .in("question_id", questionIdsList));

        List<QuestionRelationListVo> returnList = new LinkedList<>();
        for (KnowledgeQuestionParagraphEntity entity : relationList) {

            QuestionRelationListVo vo = new QuestionRelationListVo();
            BeanUtils.copyProperties(entity, vo);

            returnList.add(vo);
        }

        return returnList;
    }

    /**
     * 关联问题-段落
     * @param relationVo QuestionRelationVo
     */
    @Override
    public void doRelation(QuestionRelationVo relationVo) {

        String[] questionList = relationVo.getQuestionIds().split(",");

        KnowledgeDatasetEntity datasetInfo = datasetMapper.selectById(relationVo.getDatasetId());

        for (String questionId : questionList) {
            // 新增段落关联
            if (relationVo.getType().equals(1)) {
                // 牵扯到批量操作的情况，此处防止重复添加
                KnowledgeQuestionParagraphEntity questionParagraphRes = knowledgeQuestionParagraphMapper.selectOne(
                        new QueryWrapper<KnowledgeQuestionParagraphEntity>().eq("question_id", questionId)
                                .eq("paragraph_id", relationVo.getParagraphId()));

                if (questionParagraphRes == null) {

                    KnowledgeQuestionParagraphEntity questionParagraph = new KnowledgeQuestionParagraphEntity();
                    questionParagraph.setUuid(IdUtil.randomUUID());
                    questionParagraph.setDatasetId(relationVo.getDatasetId());
                    questionParagraph.setDocumentId(relationVo.getDocumentId());
                    questionParagraph.setParagraphId(relationVo.getParagraphId());
                    questionParagraph.setQuestionId(questionId);
                    questionParagraph.setCreateTime(Tool.nowDateTime());

                    knowledgeQuestionParagraphMapper.insert(questionParagraph);

                    // 向量化问题
                    questionTask.executeAsyncTask(questionId, relationVo, datasetInfo);
                }
            } else { // 删除关联

                knowledgeQuestionParagraphMapper.delete(new QueryWrapper<KnowledgeQuestionParagraphEntity>()
                        .eq("question_id", questionId)
                        .eq("paragraph_id", relationVo.getParagraphId()));

                // 删除embedding数据
                knowledgeEmbeddingMapper.delete(new QueryWrapper<KnowledgeEmbeddingEntity>()
                        .eq("source_type", SourceType.QUESTION.getCode())
                        .eq("source_id", questionId)
                        .eq("paragraph_id", relationVo.getParagraphId()));
            }
        }
    }

    /**
     * 获取问题关联信息
     * @param questionId String
     * @param datasetId String
     * @return List<QuestionParagraphDataVo>
     */
    @Override
    public List<QuestionParagraphDataVo> getRelationDataList(String questionId, String datasetId) {

        List<KnowledgeQuestionParagraphEntity> relationList = knowledgeQuestionParagraphMapper.selectList(
                new QueryWrapper<KnowledgeQuestionParagraphEntity>()
                .eq("question_id", questionId)
                .eq("dataset_id", datasetId));

        List<String> questionIdsList = relationList.stream().map(KnowledgeQuestionParagraphEntity::getParagraphId)
                .collect(Collectors.toList());

        List<QuestionParagraphDataVo> returnList = new LinkedList<>();
        if (!questionIdsList.isEmpty()) {
            List<KnowledgeParagraphEntity> questionList = knowledgeParagraphMapper.selectByIds(questionIdsList);
            for (KnowledgeParagraphEntity entity : questionList) {

                QuestionParagraphDataVo vo = new QuestionParagraphDataVo();
                vo.setTitle(entity.getTitle());
                vo.setContent(entity.getContent());
                vo.setParagraphId(entity.getParagraphId());
                vo.setDocumentId(entity.getDocumentId());

                returnList.add(vo);
            }
        }

        return returnList;
    }

    /**
     * 编辑问题内容
     * @param questionContentVo QuestionContentVo
     */
    @Override
    public void updateContent(QuestionContentVo questionContentVo) {

        if (questionContentVo.getContent().isBlank()) {
            throw new BusinessException("问题内容不能为空");
        }

        KnowledgeQuestionEntity questionEntity = knowledgeQuestionMapper.selectById(questionContentVo.getQuestionId());
        questionEntity.setContent(questionContentVo.getContent());
        questionEntity.setUpdateTime(Tool.nowDateTime());
        knowledgeQuestionMapper.updateById(questionEntity);

        // 查询是否关联了段落
        List<KnowledgeQuestionParagraphEntity> relationList = knowledgeQuestionParagraphMapper.selectList(
                new QueryWrapper<KnowledgeQuestionParagraphEntity>().eq("question_id", questionContentVo.getQuestionId()));

        KnowledgeDatasetEntity datasetInfo = datasetMapper.selectById(questionEntity.getDatasetId());

        if (!CollectionUtils.isEmpty(relationList)) {
            for (KnowledgeQuestionParagraphEntity entity : relationList) {
                // 向量化问题
                QuestionRelationVo questionRelationVo = new QuestionRelationVo();
                questionRelationVo.setParagraphId(entity.getParagraphId());
                questionRelationVo.setDocumentId(entity.getDocumentId());
                questionTask.executeAsyncTask(entity.getQuestionId(), questionRelationVo, datasetInfo);
            }
        }
    }

    /**
     * 编辑问题内容
     * @param questionIds String
     */
    @Override
    @Transactional
    public void deleteQuestions(String questionIds) {

        List<String> questionIdList = Arrays.stream(questionIds.split(",")).toList();
        if (CollectionUtils.isEmpty(questionIdList)) {
            throw new BusinessException("问题id不能为空");
        }

        knowledgeQuestionMapper.deleteByIds(questionIdList);

        knowledgeQuestionParagraphMapper.delete(new QueryWrapper<KnowledgeQuestionParagraphEntity>()
                        .in("question_id", questionIdList));

        knowledgeEmbeddingMapper.delete(new QueryWrapper<KnowledgeEmbeddingEntity>()
                        .eq("source_type", SourceType.QUESTION.getCode())
                        .in("source_id", questionIdList));
    }
}
