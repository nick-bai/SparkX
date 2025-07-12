// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.service.service.interfaces.dataset;

import sparkx.common.core.PageResult;
import sparkx.service.vo.question.*;

import java.util.List;

/**
 * <p>
 * 知识库问题表 服务类
 * </p>
 *
 * @author NickBai
 * @since 2025-03-05
 */
public interface IKnowledgeQuestionService {

    /**
     * 获取问题列表
     * @param queryVo QuestionQueryVo
     * @return PageResult<QuestionListVo>
     */
    PageResult<QuestionListVo> getQuestionList(QuestionQueryVo queryVo);

    /**
     * 添加问题
     * @param questionSaveVo QuestionSaveVo
     */
    void addQuestion(QuestionSaveVo questionSaveVo);

    /**
     * 获取关联信息
     * @param questionIds String
     * @param datasetId String
     * @return List<QuestionRelationListVo>
     */
    List<QuestionRelationListVo> getRelationList(String questionIds, String datasetId);

    /**
     * 关联问题-段落
     * @param relationVo QuestionRelationVo
     */
    void doRelation(QuestionRelationVo relationVo);

    /**
     * 获取问题关联信息
     * @param questionId String
     * @param datasetId String
     * @return List<QuestionParagraphDataVo>
     */
    List<QuestionParagraphDataVo> getRelationDataList(String questionId, String datasetId);

    /**
     * 编辑问题内容
     * @param questionContentVo QuestionContentVo
     */
    void updateContent(QuestionContentVo questionContentVo);

    /**
     * 删除问题
     * @param questionIds String
     */
    void deleteQuestions(String questionIds);
}
