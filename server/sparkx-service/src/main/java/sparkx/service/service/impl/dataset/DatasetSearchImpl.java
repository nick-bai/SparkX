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

import cn.hutool.json.JSONUtil;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import sparkx.common.exception.BusinessException;
import sparkx.common.utils.TsVectorGenerator;
import sparkx.service.entity.dataset.KnowledgeDatasetEntity;
import sparkx.service.entity.dataset.KnowledgeDocumentEntity;
import sparkx.service.entity.dataset.KnowledgeParagraphEntity;
import sparkx.service.helper.ApplicationHelper;
import sparkx.service.helper.EmbeddingModelBuildHelper;
import sparkx.service.mapper.dataset.KnowledgeDatasetMapper;
import sparkx.service.mapper.dataset.KnowledgeDocumentMapper;
import sparkx.service.mapper.dataset.KnowledgeEmbeddingMapper;
import sparkx.service.mapper.dataset.KnowledgeParagraphMapper;
import sparkx.service.service.interfaces.dataset.IDatasetSearchService;
import sparkx.service.vo.dataset.DatasetSearchVo;
import sparkx.service.vo.dataset.SearchVo;

import java.util.*;

@Service
public class DatasetSearchImpl implements IDatasetSearchService {

    @Autowired
    KnowledgeEmbeddingMapper knowledgeEmbeddingMapper;

    @Autowired
    KnowledgeDocumentMapper knowledgeDocumentMapper;

    @Autowired
    KnowledgeParagraphMapper knowledgeParagraphMapper;

    @Autowired
    KnowledgeDatasetMapper knowledgeDatasetMapper;

    @Autowired
    EmbeddingModelBuildHelper embeddingModelBuildHelper;

    @Autowired
    ApplicationHelper applicationHelper;

    /**
     * 命中测试
     * @param datasetSearchVo DatasetSearchVo
     */
    @Override
    public List<SearchVo> search(DatasetSearchVo datasetSearchVo) {

        if (datasetSearchVo.getKeyword().isBlank()) {
            throw new BusinessException("输入的问题不能为空");
        }

        if (datasetSearchVo.getSimilarity() < 0) {
            throw new BusinessException("设信度应该大于0");
        }

        if (datasetSearchVo.getTopRank() < 1) {
            throw new BusinessException("召回数应该大于1");
        }

        List<SearchVo> searchRes = new LinkedList<>();

        // 取对应的embedding模型
        String datasetId = datasetSearchVo.getDatasetIds().split(",")[0];
        KnowledgeDatasetEntity datasetInfo = knowledgeDatasetMapper.selectById(datasetId);
        EmbeddingModel embeddingModel = embeddingModelBuildHelper.build(datasetInfo);

        // 文本检索
        if (datasetSearchVo.getType().equals("text")) {

            searchRes = textSearch(datasetSearchVo);
        } else {

            Response<Embedding> response = embeddingModel.embed(datasetSearchVo.getKeyword());
            List<Float> vector = response.content().vectorAsList();
            // 记录token消耗记录
            applicationHelper.writeEmbeddingTokensLog(datasetInfo, response, "embedding");

            // 向量检索
            if (datasetSearchVo.getType().equals("embedding")) {

                searchRes = embeddingSearch(datasetSearchVo, vector);
            } else if (datasetSearchVo.getType().equals("mix")) { // 混合检索

                searchRes = mixSearch(datasetSearchVo, vector);
            }
        }

        return searchRes;
    }

    /**
     * 向量检索
     * @param datasetSearchVo DatasetSearchVo
     * @param vector List<Float>
     * @return List<SearchVo>
     */
    private List<SearchVo> embeddingSearch(DatasetSearchVo datasetSearchVo, List<Float> vector) {

        List<String> datasetIds = Arrays.stream(datasetSearchVo.getDatasetIds().split(",")).toList();
        List<SearchVo> searchRes = knowledgeEmbeddingMapper.embeddingSearch(JSONUtil.toJsonStr(vector), datasetIds,
                datasetSearchVo.getSimilarity(), datasetSearchVo.getTopRank());

        if (!CollectionUtils.isEmpty(searchRes)) {
            return buildFinalRes(searchRes);
        }

        return searchRes;
    }

    /**
     * 全文检索
     * @param datasetSearchVo DatasetSearchVo
     * @return List<SearchVo>
     */
    private List<SearchVo> textSearch(DatasetSearchVo datasetSearchVo) {

        String searchKeywords = TsVectorGenerator.toTsQuery(datasetSearchVo.getKeyword());
        List<String> datasetIds = Arrays.stream(datasetSearchVo.getDatasetIds().split(",")).toList();
        List<SearchVo> searchRes = knowledgeEmbeddingMapper.textSearch(searchKeywords, datasetIds, datasetSearchVo.getSimilarity(),
                datasetSearchVo.getTopRank());

        if (!CollectionUtils.isEmpty(searchRes)) {
            return buildFinalRes(searchRes);
        }

        return searchRes;
    }

    /**
     * 混合检索
     * @param datasetSearchVo DatasetSearchVo
     * @param vector List<Float>
     * @return List<SearchVo>
     */
    private List<SearchVo> mixSearch(DatasetSearchVo datasetSearchVo, List<Float> vector) {

        List<String> datasetIds = Arrays.stream(datasetSearchVo.getDatasetIds().split(",")).toList();
        String searchKeywords = TsVectorGenerator.toTsQuery(datasetSearchVo.getKeyword());
        List<SearchVo> searchRes = knowledgeEmbeddingMapper.mixSearch(JSONUtil.toJsonStr(vector), searchKeywords, datasetIds,
                datasetSearchVo.getSimilarity(), datasetSearchVo.getTopRank());

        if (!CollectionUtils.isEmpty(searchRes)) {
            return buildFinalRes(searchRes);
        }

        return searchRes;
    }

    /**
     * 构建最终的信息
     * @param searchRes List<SearchVo>
     * @return List<SearchVo>
     */
    private List<SearchVo> buildFinalRes(List<SearchVo> searchRes) {
        // 所有的文档
        List<String> documentIds = searchRes.stream().map(SearchVo::getDocumentId).toList();
        // 所有的段落
        List<String> paragraphIds = searchRes.stream().map(SearchVo::getParagraphId).toList();

        List<KnowledgeDocumentEntity> documentList = knowledgeDocumentMapper.selectByIds(documentIds);
        Map<String, String> documentId2Name = new HashMap<>();
        for (KnowledgeDocumentEntity knowledgeDocumentEntity : documentList) {
            documentId2Name.put(knowledgeDocumentEntity.getDocumentId(), knowledgeDocumentEntity.getName());
        }

        List<KnowledgeParagraphEntity> paragraphList = knowledgeParagraphMapper.selectByIds(paragraphIds);
        Map<String, KnowledgeParagraphEntity> paragraphId2Info = new HashMap<>();
        for (KnowledgeParagraphEntity knowledgeParagraphEntity : paragraphList) {
            paragraphId2Info.put(knowledgeParagraphEntity.getParagraphId(), knowledgeParagraphEntity);
        }

        for (SearchVo searchVo : searchRes) {
            // 文档标题
            searchVo.setDocumentName(documentId2Name.get(searchVo.getDocumentId()));
            // 段落内容
            searchVo.setTitle(paragraphId2Info.get(searchVo.getParagraphId()).getTitle());
            searchVo.setContent(paragraphId2Info.get(searchVo.getParagraphId()).getContent());
        }

        return searchRes;
    }
}
