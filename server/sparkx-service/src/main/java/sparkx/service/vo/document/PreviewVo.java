// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.service.vo.document;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serial;
import java.io.Serializable;

@Data
public class PreviewVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 上传的文件
     */
    private MultipartFile[] files;

    /**
     * 拆分规则
     */
    private String pattern;

    /**
     * 切割长度
     */
    private Integer splitLen;

    /**
     * 导入时把标题关联成问题
     */
    private Boolean addTitle;

    /**
     * 查分类型 1:默认 2:自定义
     */
    private Integer splitType;

    /**
     * 自动清洗
     */
    private Integer autoClean;

    /**
     * 文档模式 text:文本文件 excel:Excel表格 qa:QA问答
     */
    private String fileType;

    /**
     * 所属知识库id
     */
    private String datasetId;
}
