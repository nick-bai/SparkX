// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.service.vo.application;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
public class CensusVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户数
     */
    private Long userNum;

    /**
     * 提问数
     */
    private Long questionNum;

    /**
     * token数
     */
    private Long tokensNum;

    /**
     * 赞的数量
     */
    private Long likeNum;

    /**
     * 踩的数量
     */
    private Long dislikeNum;

    /**
     * 时间线
     */
    private List<String> timeLine;

    /**
     * 用户统计数据折线
     */
    private CensusSeriesVo userSeries;

    /**
     * 问题统计数据折线
     */
    private CensusSeriesVo questionSeries;

    /**
     * 用户tokens数据折线
     */
    private CensusSeriesVo tokensSeries;

    /**
     * 评价数据折线
     */
    private CensusSeriesVo likeSeries;

    /**
     * 评价数据折线
     */
    private CensusSeriesVo dislikeSeries;

    @Data
    public static class CensusSeriesVo {

        private List<Long> data;

        private String type;

        private boolean smooth;
    }
}