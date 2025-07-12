// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.service.vo.dataset;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class DatasetSearchVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 搜索内容
     */
    private String keyword;

    /**
     * 检索方式 embedding:向量检索 text:全文检索 mix:混合检索
     */
    private String type;

    /**
     * 相似度
     */
    private double similarity;

    /**
     * 召回条数
     */
    private Integer topRank;

    /**
     * 知识库ids
     */
    private String datasetIds;
}
