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
import sparkx.service.vo.document.*;

import java.util.List;

/**
 * <p>
 * 知识库文档表 服务类
 * </p>
 *
 * @author NickBai
 * @since 2025-03-05
 */
public interface IKnowledgeDocumentService {

    /**
     * 获取文档列表
     * @param queryVo QueryVo
     * @return PageResult<DocumentListVo>
     */
    PageResult<DocumentListVo> getDocumentList(DocumentQueryVo queryVo);

    /**
     * 预览文件
     * @param previewVo PreviewVo
     * @return List<DocumentSplitVo>
     */
    List<DocumentSplitVo> previewFile(PreviewVo previewVo);

    /**
     * 上传文件
     * @param previewVo PreviewVo
     */
    void uploadFile(PreviewVo previewVo);

    /**
     * 保存文档
     * @param documentSaveVo DocumentSaveVo
     */
    void saveDocument(DocumentSaveVo documentSaveVo);

    /**
     * 向量化文本
     * @param documentIds String
     */
    void doEmbedding(String documentIds);

    /**
     * 设置模型
     * @param settingVo DocumentSettingVo
     */
    void setDocument(DocumentSettingVo settingVo);

    /**
     * 删除文档
     * @param documentIds String
     */
    void delDocumentByIds(String documentIds);

    /**
     * 生成问题
     * @param questionVo QuestionVo
     */
    void makeQuestion(QuestionVo questionVo);
}
