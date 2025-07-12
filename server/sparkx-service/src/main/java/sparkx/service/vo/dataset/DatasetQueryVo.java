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
public class DatasetQueryVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 知识库标题
     */
    private String title;

    /**
     * 应用类型
     */
    private Integer type;

    /**
     * 分页号
     */
    private long page;

    /**
     * 每页大小
     */
    private long limit;


}