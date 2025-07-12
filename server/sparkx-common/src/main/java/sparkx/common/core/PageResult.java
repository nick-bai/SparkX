// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.common.core;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class PageResult<T> {

    /** 总记录数 **/
    private Long total;

    /** 当前页码 **/
    private Integer page;

    /** 每页条数 **/
    private Integer limit;

    /** 数据列表 **/
    private List<T> data;

    /** 扩展字段 **/
    private Map<String, Object> extend;

    /**
     * MyBatisPlus分页(数据额外处理)
     *
     * @author fzr
     * @param total   (总条数)
     * @param pageNo  (当前页码)
     * @param size    (每页条数)
     * @param list    (列表数据)
     * @param <T>     (泛型)
     * @return PageList
     */
    public static <T> PageResult<T> iPageHandle(Long total, Long pageNo, Long size, List<T> list) {
        PageResult<T> pageResult = new PageResult<>();
        pageResult.setTotal(total);
        pageResult.setPage(Math.toIntExact(pageNo));
        pageResult.setLimit(Math.toIntExact(size));
        pageResult.setData(list);
        return pageResult;
    }

    /**
     * MyBatisPlus分页(数据额外处理)
     *
     * @param total   (总条数)
     * @param pageNo  (当前页码)
     * @param size    (每页条数)
     * @param list    (列表数据)
     * @param extend  (扩展字段)
     * @param <T>     (泛型)
     * @return PageResult<T>
     */
    public static <T> PageResult<T> iPageHandle(Long total, Long pageNo, Long size, List<T> list, Map<String,Object> extend) {
        PageResult<T> pageResult = new PageResult<>();
        pageResult.setTotal(total);
        pageResult.setPage(Math.toIntExact(pageNo));
        pageResult.setLimit(Math.toIntExact(size));
        pageResult.setData(list);
        pageResult.setExtend(extend);
        return pageResult;
    }
}
