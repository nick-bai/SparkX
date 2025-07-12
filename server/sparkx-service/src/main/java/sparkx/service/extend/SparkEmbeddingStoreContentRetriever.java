// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.service.extend;

import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.ContentMetadata;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.spi.model.embedding.EmbeddingModelFactory;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.filter.Filter;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import sparkx.service.service.interfaces.dataset.IDatasetSearchService;
import sparkx.service.vo.dataset.DatasetSearchVo;
import sparkx.service.vo.dataset.SearchVo;

import java.util.*;
import java.util.function.Function;

import static dev.langchain4j.internal.Utils.getOrDefault;
import static dev.langchain4j.internal.ValidationUtils.*;
import static dev.langchain4j.spi.ServiceHelper.loadFactories;

/**
 * 复制dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever并做了少许改动；
 * 对embedding的查询进行了个性化的改造
 */
@Slf4j
public class SparkEmbeddingStoreContentRetriever implements ContentRetriever {
    public static final Function<Query, Integer> DEFAULT_MAX_RESULTS = (query) -> 3;
    public static final Function<Query, Double> DEFAULT_MIN_SCORE = (query) -> 0.0;
    public static final Function<Query, Filter> DEFAULT_FILTER = (query) -> null;

    public static final String DEFAULT_DISPLAY_NAME = "Default";

    //private final EmbeddingStore<TextSegment> embeddingStore;
    //private final EmbeddingModel embeddingModel;

    private final Function<Query, Integer> maxResultsProvider;
    private final Function<Query, Double> minScoreProvider;
    private final Function<Query, Filter> filterProvider;

    private final String displayName;

    private final IDatasetSearchService searchService;

    private final DatasetSearchVo searchDataVo;

    public SparkEmbeddingStoreContentRetriever(EmbeddingStore<TextSegment> embeddingStore,
                                               EmbeddingModel embeddingModel,
                                               IDatasetSearchService searchService,
                                               DatasetSearchVo searchDataVo) {
        this(
                DEFAULT_DISPLAY_NAME,
                embeddingStore,
                embeddingModel,
                DEFAULT_MAX_RESULTS,
                DEFAULT_MIN_SCORE,
                DEFAULT_FILTER,
                searchService,
                searchDataVo
        );
    }

    public SparkEmbeddingStoreContentRetriever(EmbeddingStore<TextSegment> embeddingStore,
                                               EmbeddingModel embeddingModel,
                                               int maxResults,
                                               IDatasetSearchService searchService,
                                               DatasetSearchVo searchDataVo) {
        this(
                DEFAULT_DISPLAY_NAME,
                embeddingStore,
                embeddingModel,
                (query) -> maxResults,
                DEFAULT_MIN_SCORE,
                DEFAULT_FILTER,
                searchService,
                searchDataVo
        );
    }

    public SparkEmbeddingStoreContentRetriever(EmbeddingStore<TextSegment> embeddingStore,
                                               EmbeddingModel embeddingModel,
                                               Integer maxResults,
                                               Double minScore,
                                               IDatasetSearchService searchService,
                                               DatasetSearchVo searchDataVo) {
        this(
                DEFAULT_DISPLAY_NAME,
                embeddingStore,
                embeddingModel,
                (query) -> maxResults,
                (query) -> minScore,
                DEFAULT_FILTER,
                searchService,
                searchDataVo
        );
    }

    @Builder
    private SparkEmbeddingStoreContentRetriever(String displayName,
                                                EmbeddingStore<TextSegment> embeddingStore,
                                                EmbeddingModel embeddingModel,
                                                Function<Query, Integer> dynamicMaxResults,
                                                Function<Query, Double> dynamicMinScore,
                                                Function<Query, Filter> dynamicFilter,
                                                IDatasetSearchService searchService,
                                                DatasetSearchVo searchDataVo) {
        this.displayName = getOrDefault(displayName, DEFAULT_DISPLAY_NAME);
        /*this.embeddingStore = ensureNotNull(embeddingStore, "embeddingStore");
        this.embeddingModel = ensureNotNull(
                getOrDefault(embeddingModel, SparkEmbeddingStoreContentRetriever::loadEmbeddingModel),
                "embeddingModel"
        );*/
        this.maxResultsProvider = getOrDefault(dynamicMaxResults, DEFAULT_MAX_RESULTS);
        this.minScoreProvider = getOrDefault(dynamicMinScore, DEFAULT_MIN_SCORE);
        this.filterProvider = getOrDefault(dynamicFilter, DEFAULT_FILTER);
        this.searchService = searchService;
        this.searchDataVo = searchDataVo;
    }

    private static EmbeddingModel loadEmbeddingModel() {
        Collection<EmbeddingModelFactory> factories = loadFactories(EmbeddingModelFactory.class);
        if (factories.size() > 1) {
            throw new RuntimeException("Conflict: multiple embedding models have been found in the classpath. " +
                    "Please explicitly specify the one you wish to use.");
        }

        for (EmbeddingModelFactory factory : factories) {
            return factory.create();
        }

        return null;
    }

    public static class SparkEmbeddingStoreContentRetrieverBuilder {

        public SparkEmbeddingStoreContentRetrieverBuilder maxResults(Integer maxResults) {
            if (maxResults != null) {
                dynamicMaxResults = (query) -> ensureGreaterThanZero(maxResults, "maxResults");
            }
            return this;
        }

        public SparkEmbeddingStoreContentRetrieverBuilder minScore(Double minScore) {
            if (minScore != null) {
                dynamicMinScore = (query) -> ensureBetween(minScore, 0, 1, "minScore");
            }
            return this;
        }

        public SparkEmbeddingStoreContentRetrieverBuilder filter(Filter filter) {
            if (filter != null) {
                dynamicFilter = (query) -> filter;
            }
            return this;
        }
    }

    /**
     * Creates an instance of an {@code EmbeddingStoreContentRetriever} from the specified {@link EmbeddingStore}
     * and {@link EmbeddingModel} found through SPI (see {@link EmbeddingModelFactory}).
     */
    public static SparkEmbeddingStoreContentRetriever from(EmbeddingStore<TextSegment> embeddingStore) {
        return builder().embeddingStore(embeddingStore).build();
    }

    /**
     * SparkX 对这里进行了改造
     */
    @Override
    public List<Content> retrieve(Query query) {

        UserMessage userMessage = (UserMessage) query.metadata().chatMessage();
        searchDataVo.setKeyword(((TextContent) userMessage.contents().get(0)).text()); // 原输入语句
        searchDataVo.setSimilarity(minScoreProvider.apply(query)); // 相似度
        searchDataVo.setTopRank(maxResultsProvider.apply(query)); // 召回数量

        List<SearchVo> dbSearchRes = searchService.search(searchDataVo);
        List<Content> searchResult = new LinkedList<>();
        for (SearchVo searchVo : dbSearchRes) {

            Content content =  Content.from(
                    TextSegment.from(searchVo.getContent()),
                    Map.of(
                            ContentMetadata.SCORE, searchVo.getComprehensiveScore(),
                            ContentMetadata.EMBEDDING_ID, searchVo.getParagraphId()
                    )
            );

            searchResult.add(content);
        }

        return searchResult;
    }

    @Override
    public String toString() {
        return "SparkEmbeddingStoreContentRetriever{" +
                "displayName='" + displayName + '\'' +
                '}';
    }
}

