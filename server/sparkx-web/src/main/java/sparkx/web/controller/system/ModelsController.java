// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.web.controller.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sparkx.common.core.AjaxResult;
import sparkx.service.service.interfaces.system.IModelsService;
import sparkx.service.vo.system.ModelVo;
import sparkx.service.vo.system.ModelsInfoVo;
import sparkx.service.vo.system.ModelsVo;

import java.util.List;

@RequestMapping("api/models")
@RestController
public class ModelsController {

    @Autowired
    IModelsService iModelsService;

    /**
     * 模型列表
     */
    @GetMapping("/list")
    public AjaxResult<List<ModelsVo>> modelList(@RequestParam("type") Integer type, @RequestParam("status") Integer status) {

        return AjaxResult.success(iModelsService.getModelList(type, status));
    }

    /**
     * 模型信息
     */
    @GetMapping("/info")
    public AjaxResult<ModelsInfoVo> modeInfo(@RequestParam("modelId") String modelId) {

        return AjaxResult.success(iModelsService.getModelInfo(modelId));
    }

    /**
     * 编辑模型
     */
    @PostMapping("/edit")
    public AjaxResult<Object> edit(@RequestBody ModelsInfoVo modelsInfoVo) {

        iModelsService.editModel(modelsInfoVo);
        return AjaxResult.success();
    }

    /**
     * 获取重排模型
     */
    @GetMapping("/rerankList")
    public AjaxResult<List<ModelVo>> getRerankList() {

        return AjaxResult.success(iModelsService.getRerankList());
    }
}