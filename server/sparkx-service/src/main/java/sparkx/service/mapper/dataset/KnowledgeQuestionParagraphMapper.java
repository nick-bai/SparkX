// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.service.mapper.dataset;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import sparkx.common.core.IBaseMapper;
import sparkx.service.entity.dataset.KnowledgeQuestionParagraphEntity;

import java.util.List;

/**
 * 段落问题关联表 Mapper
 */
@Mapper
public interface KnowledgeQuestionParagraphMapper extends IBaseMapper<KnowledgeQuestionParagraphEntity> {

    @Delete({
            "<script>",
            "DELETE FROM knowledge_question_paragraph",
            "WHERE document_id IN",
            "<foreach item='id' collection='list' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            "</script>"
    })
    void deleteByDocumentIds(@Param("list") List<String> documentIds);

    @Update({
            "<script>",
            "UPDATE knowledge_question_paragraph SET dataset_id = #{datasetId} ",
            "WHERE document_id IN",
            "<foreach item='id' collection='list' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            "</script>"
    })
    void updateDatasetByIds(@Param("list") List<String> documentIds, @Param("datasetId") String datasetId);
}
