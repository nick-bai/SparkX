// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.web.controller.dataset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sparkx.common.core.AjaxResult;
import sparkx.common.core.PageResult;
import sparkx.service.service.interfaces.dataset.IKnowledgeDocumentService;
import sparkx.service.vo.document.*;

import java.util.List;

@RequestMapping("/api/document")
@RestController
public class DocumentController {

    @Autowired
    IKnowledgeDocumentService iKnowledgeDocumentService;

    /**
     * 知识库文档列表
     */
    @GetMapping("/list")
    public AjaxResult<PageResult<DocumentListVo>> list(DocumentQueryVo queryVo) {

        return AjaxResult.success(iKnowledgeDocumentService.getDocumentList(queryVo));
    }

    /**
     * 文档分段预览
     */
    @PostMapping("/preview")
    public AjaxResult<List<DocumentSplitVo>> preview(PreviewVo previewVo) {

        return AjaxResult.success(iKnowledgeDocumentService.previewFile(previewVo));
    }

    /**
     * 直接导入excel文档
     */
    @PostMapping("/uploadFile")
    public AjaxResult<Object> uploadFile(PreviewVo previewVo) {

        iKnowledgeDocumentService.uploadFile(previewVo);
        return AjaxResult.success();
    }

    /**
     * 保存文档入库
     */
    @PostMapping("/save")
    public AjaxResult<Object> save(@RequestBody DocumentSaveVo saveVo) {

        iKnowledgeDocumentService.saveDocument(saveVo);
        return AjaxResult.success();
    }

    /**
     * 向量化文档
     */
    @GetMapping("/embedding")
    public AjaxResult<Object> embeddings(@RequestParam("documentIds") String documentIds) {

        iKnowledgeDocumentService.doEmbedding(documentIds);
        return AjaxResult.success();
    }

    /**
     * 设置应答模式
     */
    @PostMapping("/setting")
    public AjaxResult<Object> setting(@RequestBody DocumentSettingVo settingVo) {

        iKnowledgeDocumentService.setDocument(settingVo);
        return AjaxResult.success();
    }

    /**
     * 删除文档
     */
    @GetMapping("/del")
    public AjaxResult<Object> del(@RequestParam("documentIds") String documentIds) {

        iKnowledgeDocumentService.delDocumentByIds(documentIds);
        return AjaxResult.success();
    }

    /**
     * 生成问题
     */
    @PostMapping("/question")
    public AjaxResult<Object> question(@RequestBody QuestionVo questionVo) {

        iKnowledgeDocumentService.makeQuestion(questionVo);
        return AjaxResult.success();
    }
}
