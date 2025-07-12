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

import org.apache.ibatis.annotations.*;
import sparkx.common.core.IBaseMapper;
import sparkx.service.entity.dataset.KnowledgeEmbeddingEntity;
import sparkx.service.vo.dataset.SearchVo;

import java.util.List;

/**
 * 向量索引表 Mapper
 */
@Mapper
public interface KnowledgeEmbeddingMapper extends IBaseMapper<KnowledgeEmbeddingEntity> {

    @Delete({
            "<script>",
            "DELETE FROM knowledge_embedding",
            "WHERE document_id IN",
            "<foreach item='id' collection='list' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            "</script>"
    })
    void deleteByDocumentIds(@Param("list") List<String> documentIds);

    @Select({
        "<script>",
            "SELECT paragraph_id,document_id,comprehensive_score,comprehensive_score as similarity FROM (SELECT DISTINCT ON (\"paragraph_id\") ( similarity ),* ,similarity AS comprehensive_score",
            " FROM ( SELECT *, ( 1 - ( knowledge_embedding.embedding <![CDATA[ <=>  ]]> #{vector} ) ) AS similarity FROM knowledge_embedding WHERE knowledge_embedding.dataset_id IN ",
            "<foreach item='datasetId' collection='datasetIds' open='(' separator=',' close=')'>",
            "#{datasetId}",
            "</foreach>",
            " AND knowledge_embedding.active = 1) TEMP",
            " ORDER BY paragraph_id,similarity DESC) DISTINCT_TEMP",
            " WHERE comprehensive_score > #{score} ORDER BY comprehensive_score DESC LIMIT #{limit}",
        "</script>"
    })
    List<SearchVo> embeddingSearch(@Param("vector") String vector, @Param("datasetIds") List<String> datasetIds,
                                   @Param("score") double score, @Param("limit") int limit);
    @Select({
            "<script>",
            "SELECT paragraph_id,document_id,comprehensive_score,comprehensive_score as similarity FROM (SELECT DISTINCT ON (\"paragraph_id\") ( similarity ),* ,similarity AS comprehensive_score",
            " FROM ( SELECT *,ts_rank_cd(knowledge_embedding.search_vector,websearch_to_tsquery('simple', #{tsQuery}),32) AS similarity  FROM knowledge_embedding WHERE knowledge_embedding.dataset_id IN ",
            "<foreach item='datasetId' collection='datasetIds' open='(' separator=',' close=')'>",
            "#{datasetId}",
            "</foreach>",
            " AND knowledge_embedding.active = 1) TEMP",
            " ORDER BY paragraph_id,similarity DESC) DISTINCT_TEMP",
            " WHERE comprehensive_score > #{score} ORDER BY comprehensive_score DESC LIMIT #{limit}",
            "</script>"
    })
    List<SearchVo> textSearch(@Param("tsQuery") String tsQuery, @Param("datasetIds") List<String> datasetIds,
                              @Param("score") double score, @Param("limit") int limit);

    @Select({
            "<script>",
            "SELECT paragraph_id,document_id,comprehensive_score,comprehensive_score as similarity FROM (SELECT DISTINCT ON (\"paragraph_id\") ( similarity ),* ,similarity AS comprehensive_score",
            " FROM ( SELECT *, ( (1 - ( knowledge_embedding.embedding <![CDATA[ <=>  ]]> #{vector} )) + ts_rank_cd(knowledge_embedding.search_vector,websearch_to_tsquery('simple', #{tsQuery}),32) ) AS similarity FROM knowledge_embedding WHERE knowledge_embedding.dataset_id IN ",
            "<foreach item='datasetId' collection='datasetIds' open='(' separator=',' close=')'>",
            "#{datasetId}",
            "</foreach>",
            " AND knowledge_embedding.active = 1) TEMP",
            " ORDER BY paragraph_id,similarity DESC) DISTINCT_TEMP",
            " WHERE comprehensive_score > #{score} ORDER BY comprehensive_score DESC LIMIT #{limit}",
            "</script>"
    })
    List<SearchVo> mixSearch(@Param("vector") String vector, @Param("tsQuery") String tsQuery, @Param("datasetIds") List<String> datasetIds,
                                   @Param("score") double score, @Param("limit") int limit);

    @Update({
            "<script>",
            "UPDATE knowledge_embedding SET dataset_id = #{datasetId} ",
            "WHERE document_id IN",
            "<foreach item='id' collection='list' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            "</script>"
    })
    void updateDatasetByIds(@Param("list") List<String> documentIds, @Param("datasetId") String datasetId);
}
