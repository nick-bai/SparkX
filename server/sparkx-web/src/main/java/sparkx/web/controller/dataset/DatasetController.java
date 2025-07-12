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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sparkx.common.core.AjaxResult;
import sparkx.common.core.PageResult;
import sparkx.service.service.interfaces.dataset.IDatasetSearchService;
import sparkx.service.service.interfaces.dataset.IKnowledgeDatasetService;
import sparkx.service.validate.dataset.DatasetValidate;
import sparkx.service.vo.dataset.*;

import java.util.List;

@RequestMapping("/api/dataset")
@RestController
public class DatasetController {

    @Autowired
    IKnowledgeDatasetService iKnowledgeDatasetService;

    @Autowired
    IDatasetSearchService iDatasetSearchService;

    /**
     * 知识库列表
     */
    @GetMapping("/list")
    public AjaxResult<PageResult<DatasetVo>> list(DatasetQueryVo queryVo) {

        return AjaxResult.success(iKnowledgeDatasetService.getDatasetList(queryVo));
    }

    /**
     * 创建知识库
     */
    @PostMapping("/add")
    public AjaxResult<Object> add(@RequestBody @Validated DatasetValidate validate) {

        iKnowledgeDatasetService.addDataset(validate);
        return AjaxResult.success();
    }

    /**
     * 编辑知识库
     */
    @PostMapping("/edit")
    public AjaxResult<Object> edit(@RequestBody @Validated DatasetValidate validate) {

        iKnowledgeDatasetService.editDataset(validate);
        return AjaxResult.success();
    }

    /**
     * 命中测试
     */
    @PostMapping("/hitTest")
    public AjaxResult<List<SearchVo>> hitTest(@RequestBody DatasetSearchVo datasetSearchVo) {

        return AjaxResult.success(iDatasetSearchService.search(datasetSearchVo));
    }

    /**
     * 向量化
     */
    @GetMapping("/embedding")
    public AjaxResult<Object> embedding(@RequestParam("datasetId") String datasetId) {

        iKnowledgeDatasetService.embeddingDataset(datasetId);
        return AjaxResult.success();
    }

    /**
     * 删除知识库
     */
    @GetMapping("/del")
    public AjaxResult<Object> del(@RequestParam("datasetId") String datasetId) {

        iKnowledgeDatasetService.deleteDataset(datasetId);
        return AjaxResult.success();
    }

    /**
     * 获取其他知识库
     */
    @GetMapping("/otherDataset")
    public AjaxResult<List<OtherDatasetVo>> otherDataset(@RequestParam("datasetId") String datasetId) {

        return AjaxResult.success(iKnowledgeDatasetService.getOtherDatasetList(datasetId));
    }

    /**
     * 迁移文档
     */
    @PostMapping("/transfer")
    public AjaxResult<Object> transfer(@RequestBody TransferDatasetVo transferDatasetVo) {

        iKnowledgeDatasetService.transferDocument(transferDatasetVo);
        return AjaxResult.success();
    }
}