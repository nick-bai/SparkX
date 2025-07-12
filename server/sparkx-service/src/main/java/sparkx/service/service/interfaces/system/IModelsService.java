// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.service.service.interfaces.system;

import sparkx.service.vo.system.ModelsInfoVo;
import sparkx.service.vo.system.ModelsVo;

import java.util.List;

public interface IModelsService {

    /**
     * 获取模型列表
     * @param type Integer
     * @param status Integer
     * @return List<ModelsVo>
     */
    List<ModelsVo> getModelList(Integer type, Integer status);

    /**
     * 获取模型信息
     * @param modelId String
     * @return ModelsInfoVo
     */
    ModelsInfoVo getModelInfo(String modelId);

    /**
     * 编辑模型
     * @param modelsInfoVo ModelsInfoVo
     */
    void editModel(ModelsInfoVo modelsInfoVo);
}
