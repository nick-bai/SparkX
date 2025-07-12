// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.service.service.impl.dataset;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import sparkx.common.core.PageResult;
import sparkx.common.enums.DocumentStatusEnum;
import sparkx.common.enums.SourceType;
import sparkx.common.exception.BusinessException;
import sparkx.common.utils.Tool;
import sparkx.service.entity.application.ApplicationDatasetRelationEntity;
import sparkx.service.entity.dataset.*;
import sparkx.service.entity.system.SystemTeamUserEntity;
import sparkx.service.entity.system.SystemUsersEntity;
import sparkx.service.helper.LicenseHelper;
import sparkx.service.helper.UserContextHelper;
import sparkx.service.mapper.application.ApplicationDatasetRelationMapper;
import sparkx.service.mapper.dataset.*;
import sparkx.service.mapper.system.SystemTeamUserMapper;
import sparkx.service.mapper.system.SystemUserMapper;
import sparkx.service.service.interfaces.dataset.IKnowledgeDatasetService;
import sparkx.service.task.EmbeddingDocumentTask;
import sparkx.service.validate.dataset.DatasetValidate;
import sparkx.service.vo.application.PermissionVo;
import sparkx.service.vo.dataset.DatasetQueryVo;
import sparkx.service.vo.dataset.DatasetVo;
import sparkx.service.vo.dataset.OtherDatasetVo;
import sparkx.service.vo.dataset.TransferDatasetVo;
import sparkx.service.vo.system.LocalUserVo;

import java.util.*;

@Service
public class KnowledgeDatasetServiceImpl implements IKnowledgeDatasetService {

    @Autowired
    KnowledgeDatasetMapper datasetMapper;

    @Autowired
    SystemUserMapper userMapper;

    @Autowired
    KnowledgeDocumentMapper knowledgeDocumentMapper;

    @Autowired
    KnowledgeParagraphMapper knowledgeParagraphMapper;

    @Autowired
    KnowledgeEmbeddingMapper knowledgeEmbeddingMapper;

    @Autowired
    KnowledgeQuestionParagraphMapper knowledgeQuestionParagraphMapper;

    @Autowired
    KnowledgeQuestionMapper knowledgeQuestionMapper;

    @Autowired
    ApplicationDatasetRelationMapper applicationDatasetRelationMapper;

    @Autowired
    EmbeddingDocumentTask task;

    @Autowired
    SystemTeamUserMapper systemTeamUserMapper;

    @Autowired
    LicenseHelper licenseHelper;

    /**
     * 获取知识库列表
     * @param queryVo DatasetQueryVo
     * @return PageResult<DatasetVo>
     */
    @Override
    public PageResult<DatasetVo> getDatasetList(DatasetQueryVo queryVo) {

        long pageNo   = queryVo.getPage();
        long pageSize = queryVo.getLimit();

        QueryWrapper<KnowledgeDatasetEntity> queryWrapper = new QueryWrapper<>();

        if (!queryVo.getTitle().isBlank()) {
            queryWrapper.like("title", queryVo.getTitle());
        }

        LocalUserVo userData = UserContextHelper.getUser();
        // 查出当前用户所在的团队
        List<SystemTeamUserEntity> teamListData = systemTeamUserMapper.selectList(
                new QueryWrapper<SystemTeamUserEntity>().select("team_id").eq("user_id", userData.getUserId()));
        List<String> viewDatasetsIds = new ArrayList<>();
        List<String> otherManageDatasetsIds = new ArrayList<>();
        List<String> manageDatasetsIds = new ArrayList<>();

        // 全部的数据
        if (queryVo.getType().equals(0) || queryVo.getType().equals(2)) {

            // 获取当前用户不是管理员的团队应用ID
            List<Integer> otherTeamIds = teamListData.stream().map(SystemTeamUserEntity::getTeamId)
                    .filter(item -> !item.equals(userData.getTeamId())).toList();
            if (!CollectionUtils.isEmpty(otherTeamIds)) {
                Map<String, List<String>> otherTeamMap = getTeamDatasetsIds(otherTeamIds);
                viewDatasetsIds.addAll(otherTeamMap.get("viewIds"));
                otherManageDatasetsIds.addAll(otherTeamMap.get("manageIds"));
            }

            // 获取当前用户是管理员的团队应用ID
            List<Integer> userTeamIds = new ArrayList<>();
            userTeamIds.add(userData.getTeamId());
            Map<String, List<String>> userTeamMap = getTeamDatasetsIds(userTeamIds);
            viewDatasetsIds.addAll(userTeamMap.get("viewIds"));
            otherManageDatasetsIds.addAll(userTeamMap.get("manageIds"));

            if (queryVo.getType().equals(0)) {
                // 获取管理员自己的应用ID
                List<KnowledgeDatasetEntity> mangeDatasetsList = datasetMapper.selectList(
                        new QueryWrapper<KnowledgeDatasetEntity>().select("dataset_id").eq("user_id", userData.getUserId()));
                List<String> manageDatasetsIdList = mangeDatasetsList.stream().map(KnowledgeDatasetEntity::getDatasetId).toList();
                viewDatasetsIds.addAll(manageDatasetsIdList);
                manageDatasetsIds.addAll(manageDatasetsIdList);
            }

            // 只查可见的数据
            queryWrapper.in("dataset_id", viewDatasetsIds);
        } else if (queryVo.getType().equals(1)) { // 自己的数据
            queryWrapper.eq("user_id", userData.getUserId());
        }

        queryWrapper.orderByDesc("create_time");
        IPage<KnowledgeDatasetEntity> datasetListRes = datasetMapper.selectPage(new Page<>(pageNo, pageSize), queryWrapper);
        List<DatasetVo> datasetVoList = new LinkedList<>();

        for (KnowledgeDatasetEntity entity : datasetListRes.getRecords()) {
            DatasetVo vo = new DatasetVo();
            BeanUtils.copyProperties(entity, vo);

            SystemUsersEntity userInfo = userMapper.selectById(entity.getUserId());
            vo.setAuthor(userInfo.getNickname());

            // 文档数
            long documentNum = knowledgeDocumentMapper.selectCount(new QueryWrapper<KnowledgeDocumentEntity>()
                    .eq("dataset_id", entity.getDatasetId()));
            vo.setDocumentNum(documentNum);

            // 字符数
            KnowledgeDocumentEntity documentInfo = knowledgeDocumentMapper.selectOne(new QueryWrapper<KnowledgeDocumentEntity>()
                            .select("sum(file_size) as file_size")
                    .eq("dataset_id", entity.getDatasetId()));
            if (documentInfo != null) {
                vo.setFileSize(documentInfo.getFileSize());
            } else {
                vo.setFileSize(0);
            }

            // 应用数
            long appNum = applicationDatasetRelationMapper.selectCount(new QueryWrapper<ApplicationDatasetRelationEntity>()
                    .eq("dataset_id", entity.getDatasetId()));
            vo.setAppNum(appNum);

            // 补充权限
            if (manageDatasetsIds.contains(vo.getDatasetId())) {
                vo.setView(true);
                vo.setManage(true);
            } else {
                vo.setView(viewDatasetsIds.contains(vo.getDatasetId()));
                vo.setManage(otherManageDatasetsIds.contains(vo.getDatasetId()));
            }

            datasetVoList.add(vo);
        }

        return PageResult.iPageHandle(datasetListRes.getTotal(), pageNo, pageSize, datasetVoList);
    }

    /**
     * 添加知识库模型
     * @param validate DatasetValidate
     */
    @Override
    public void addDataset(DatasetValidate validate) {

        // 授权校验
        long totalDataset = datasetMapper.selectCount(new QueryWrapper<>());
        if (totalDataset >= licenseHelper.getDatasetNum()) {
            throw new BusinessException(403, "社区版本最多可添加" + licenseHelper.getDatasetNum() + "个知识库，如需更多知识库，请购买授权版本！");
        }

        KnowledgeDatasetEntity datasetEntity = new KnowledgeDatasetEntity();
        BeanUtils.copyProperties(validate, datasetEntity);

        LocalUserVo userData = UserContextHelper.getUser();
        datasetEntity.setType(1); // 写死通用类型
        datasetEntity.setUserId(userData.getUserId());
        datasetEntity.setDatasetId(IdUtil.randomUUID());
        datasetEntity.setEmbeddingModelId(validate.getEmbedding_model_id());
        datasetEntity.setEmbeddingModel(validate.getEmbedding_model());
        datasetEntity.setCreateTime(Tool.nowDateTime());

        datasetMapper.insert(datasetEntity);
    }

    /**
     * 编辑知识库
     * @param validate DatasetValidate
     */
    @Override
    public void editDataset(DatasetValidate validate) {

        KnowledgeDatasetEntity datasetEntity = datasetMapper.selectById(validate.getDatasetId());
        datasetEntity.setTitle(validate.getTitle());
        datasetEntity.setDescription(validate.getDescription());
        datasetEntity.setUpdateTime(Tool.nowDateTime());

        datasetMapper.updateById(datasetEntity);
    }

    /**
     * 向量化整个文档
     * @param datasetId String
     */
    @Override
    public void embeddingDataset(String datasetId) {

        List<KnowledgeDocumentEntity> documentList = knowledgeDocumentMapper.selectList(
                new QueryWrapper<KnowledgeDocumentEntity>().eq("dataset_id", datasetId));

        KnowledgeDatasetEntity datasetInfo = datasetMapper.selectById(datasetId);

        if (!CollectionUtils.isEmpty(documentList)) {

            for (KnowledgeDocumentEntity documentEntity : documentList) {
                String documentId = documentEntity.getDocumentId();
                // 检测应答模式
                KnowledgeDocumentEntity documentInfo = knowledgeDocumentMapper.selectById(documentId);
                if (documentInfo.getAnswerType().equals("model")) {

                    // 标记开始向量化
                    KnowledgeDocumentEntity updateEntity = knowledgeDocumentMapper.selectById(documentId);
                    updateEntity.setStatus(DocumentStatusEnum.RUNNING.getCode());
                    updateEntity.setUpdateTime(Tool.nowDateTime());
                    knowledgeDocumentMapper.updateById(updateEntity);

                    // 执行向量化
                    task.executeAsyncTask(documentId, datasetInfo);
                }
            }
        }
    }

    /**
     * 删除整个知识库
     * @param datasetId String
     */
    @Override
    @Transactional
    public void deleteDataset(String datasetId) {

        // 删除知识库
        datasetMapper.deleteById(datasetId);
        // 删除知识库下的文档
        knowledgeDocumentMapper.delete(new QueryWrapper<KnowledgeDocumentEntity>().eq("dataset_id", datasetId));
        // 删除知识库下的文档分段
        knowledgeParagraphMapper.delete(new QueryWrapper<KnowledgeParagraphEntity>().eq("dataset_id", datasetId));
        // 删除知识库下的文档embedding数据
        knowledgeEmbeddingMapper.delete(new QueryWrapper<KnowledgeEmbeddingEntity>().eq("dataset_id", datasetId));
        // 删除知识库下文档下问题数据
        knowledgeQuestionMapper.delete(new QueryWrapper<KnowledgeQuestionEntity>().eq("dataset_id", datasetId));
        // 删除文档下问题关联数据
        knowledgeQuestionParagraphMapper.delete(new QueryWrapper<KnowledgeQuestionParagraphEntity>().eq("dataset_id", datasetId));
    }

    /**
     * 获取其他知识库
     * @param datasetId String
     * @return List<OtherDatasetVo>
     */
    @Override
    public List<OtherDatasetVo> getOtherDatasetList(String datasetId) {

        QueryWrapper<KnowledgeDatasetEntity> queryWrapper = new QueryWrapper<>();
        LocalUserVo userData = UserContextHelper.getUser();
        queryWrapper.eq("user_id", userData.getUserId());
        queryWrapper.ne("dataset_id", datasetId);
        queryWrapper.orderByDesc("create_time");

        List<KnowledgeDatasetEntity> datasetList = datasetMapper.selectList(queryWrapper);
        List<OtherDatasetVo> otherDatasetList = new LinkedList<>();
        for (KnowledgeDatasetEntity entity : datasetList) {
            OtherDatasetVo otherDatasetVo = new OtherDatasetVo();
            BeanUtils.copyProperties(entity, otherDatasetVo);

            otherDatasetList.add(otherDatasetVo);
        }

        return otherDatasetList;
    }

    /**
     * 迁移文档
     * @param transferDatasetVo TransferDatasetVo
     */
    @Override
    @Transactional
    public void transferDocument(TransferDatasetVo transferDatasetVo) {

        if (transferDatasetVo.getDocumentIds().isBlank() || transferDatasetVo.getDatasetId().isBlank()) {
            throw new BusinessException("参数错误");
        }

        List<String> documentIds = Arrays.stream(transferDatasetVo.getDocumentIds().split(",")).toList();
        // 更改文档的知识库id
        knowledgeDocumentMapper.updateDatasetByIds(documentIds, transferDatasetVo.getDatasetId());
        // 更改段落的知识库id
        knowledgeParagraphMapper.updateDatasetByIds(documentIds, transferDatasetVo.getDatasetId());
        // 获取全部关联的问题id
        List<KnowledgeQuestionParagraphEntity> questionRelationList = knowledgeQuestionParagraphMapper.selectList(
                new QueryWrapper<KnowledgeQuestionParagraphEntity>().in("document_id", documentIds));
        List<String> questionIds = questionRelationList.stream().map(KnowledgeQuestionParagraphEntity::getQuestionId).toList();
        KnowledgeQuestionEntity questionEntity = new KnowledgeQuestionEntity();
        questionEntity.setDatasetId(transferDatasetVo.getDatasetId());
        knowledgeQuestionMapper.update(questionEntity, new QueryWrapper<KnowledgeQuestionEntity>().in("question_id", questionIds));

        // 删除问题关联的知识库id
        knowledgeQuestionParagraphMapper.updateDatasetByIds(documentIds, transferDatasetVo.getDatasetId());
        // 删除embedding的问题
        knowledgeEmbeddingMapper.delete(new QueryWrapper<KnowledgeEmbeddingEntity>()
                        .eq("source_type", SourceType.QUESTION.getCode())
                        .eq("dataset_id", transferDatasetVo.getOldDatasetId())
                        .in("document_id", documentIds));
        // 修改embedding的文本关联
        knowledgeEmbeddingMapper.updateDatasetByIds(documentIds, transferDatasetVo.getDatasetId());
    }

    /**
     * 获取团队下的应用id
     * @param teamIds teamIds
     * @return Map<List<String>, List<String>>
     */
    private Map<String, List<String>> getTeamDatasetsIds(List<Integer> teamIds) {

        Map<String, List<String>> permissionList = new HashMap<>();

        List<SystemTeamUserEntity> teamUserData = systemTeamUserMapper.selectList(
                new QueryWrapper<SystemTeamUserEntity>().select("dataset_permission").in("team_id", teamIds));

        List<String> viewIds = new ArrayList<>();
        List<String> manageIds = new ArrayList<>();
        for (SystemTeamUserEntity entity : teamUserData) {
            if (entity.getDatasetPermission().isBlank()) {
                continue;
            }

            PermissionVo permissionData = JSONUtil.toBean(entity.getDatasetPermission(), PermissionVo.class);

            if (!CollectionUtils.isEmpty(permissionData.getView())) {
                viewIds.addAll(permissionData.getView());
                manageIds.addAll(permissionData.getManage());
            }
        }

        permissionList.put("viewIds", viewIds);
        permissionList.put("manageIds", manageIds);

        return permissionList;
    }
}