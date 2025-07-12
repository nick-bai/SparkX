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
import sparkx.service.validate.dataset.DatasetValidate;
import sparkx.service.vo.dataset.DatasetQueryVo;
import sparkx.service.vo.dataset.DatasetVo;
import sparkx.service.vo.dataset.OtherDatasetVo;
import sparkx.service.vo.dataset.TransferDatasetVo;

import java.util.List;

public interface IKnowledgeDatasetService {

    /**
     * 获取知识库列表
     * @param queryVo DatasetQueryVo
     * @return PageResult<List<DatasetVo>>
     */
    PageResult<DatasetVo> getDatasetList(DatasetQueryVo queryVo);

    /**
     * 添加知识库模型
     * @param validate DatasetValidate
     */
    void addDataset(DatasetValidate validate);

    /**
     * 编辑知识库模型
     * @param validate DatasetValidate
     */
    void editDataset(DatasetValidate validate);

    /**
     * 向量化整个知识库
     * @param datasetId String
     */
    void embeddingDataset(String datasetId);

    /**
     * 删除整个知识库
     * @param datasetId String
     */
    void deleteDataset(String datasetId);

    /**
     * 获取其他知识库
     * @param datasetId String
     * @return List<OtherDatasetVo>
     */
    List<OtherDatasetVo> getOtherDatasetList(String datasetId);

    /**
     * 迁移文档
     * @param transferDatasetVo TransferDatasetVo
     */
    void transferDocument(TransferDatasetVo transferDatasetVo);
}
