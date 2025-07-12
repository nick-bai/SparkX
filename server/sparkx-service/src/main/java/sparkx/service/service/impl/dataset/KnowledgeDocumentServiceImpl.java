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
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import sparkx.common.core.PageResult;
import sparkx.common.enums.DocumentStatusEnum;
import sparkx.common.enums.StatusEnum;
import sparkx.common.exception.BusinessException;
import sparkx.common.utils.Tool;
import sparkx.service.entity.dataset.*;
import sparkx.service.entity.system.ModelsEntity;
import sparkx.service.fileSplitter.FileHandleFactory;
import sparkx.service.fileSplitter.FileHandleInterface;
import sparkx.service.mapper.dataset.*;
import sparkx.service.mapper.system.ModelsMapper;
import sparkx.service.service.interfaces.dataset.IKnowledgeDocumentService;
import sparkx.service.task.EmbeddingDocumentTask;
import sparkx.service.vo.document.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class KnowledgeDocumentServiceImpl implements IKnowledgeDocumentService{

    @Autowired
    KnowledgeDocumentMapper knowledgeDocumentMapper;

    @Autowired
    KnowledgeParagraphMapper knowledgeParagraphMapper;

    @Autowired
    KnowledgeEmbeddingMapper knowledgeEmbeddingMapper;

    @Autowired
    KnowledgeQuestionParagraphMapper knowledgeQuestionParagraphMapper;

    @Autowired
    EmbeddingDocumentTask task;

    @Autowired
    private ModelsMapper modelsMapper;

    @Autowired
    private KnowledgeQuestionMapper knowledgeQuestionMapper;

    @Autowired
    KnowledgeDatasetMapper knowledgeDatasetMapper;

    /**
     * 知识库下文档列表
     * @param queryVo DocumentQueryVo
     * @return PageResult<DocumentListVo>
     */
    @Override
    public PageResult<DocumentListVo> getDocumentList(DocumentQueryVo queryVo) {

        long pageNo   = queryVo.getPage();
        long pageSize = queryVo.getLimit();

        QueryWrapper<KnowledgeDocumentEntity> queryWrapper = new QueryWrapper<>();

        if (!queryVo.getName().isBlank()) {
            queryWrapper.like("name", queryVo.getName());
        }
        queryWrapper.eq("dataset_id", queryVo.getDatasetId());

        queryWrapper.orderByDesc("create_time");
        IPage<KnowledgeDocumentEntity> datasetListRes = knowledgeDocumentMapper.selectPage(new Page<>(pageNo, pageSize), queryWrapper);

        List<DocumentListVo> datasetVoList = new LinkedList<>();
        for (KnowledgeDocumentEntity entity : datasetListRes.getRecords()) {
            DocumentListVo vo = new DocumentListVo();
            BeanUtils.copyProperties(entity, vo);

            datasetVoList.add(vo);
        }

        return PageResult.iPageHandle(datasetListRes.getTotal(), pageNo, pageSize, datasetVoList);
    }

    /**
     * 预览文件
     * @param previewVo PreviewVo
     * @return List<DocumentSplitVo>
     */
    @Override
    public List<DocumentSplitVo> previewFile(PreviewVo previewVo) {

        try {

            List<DocumentSplitVo> splitList = new LinkedList<>();
            for (MultipartFile file : previewVo.getFiles()) {

                DocumentSplitVo vo = new DocumentSplitVo();
                // 文本标题
                String originalFilename = file.getOriginalFilename();
                vo.setName(originalFilename);

                byte[] bytes = file.getBytes(); // 获取文件的字节数组

                // 文本大小
                vo.setFileSize(file.getSize());

                FileHandleFactory fileHandleFactory = new FileHandleFactory();

                // 选择文件处理器
                String ext = originalFilename.split("\\.")[1];
                FileHandleInterface fileHandle = fileHandleFactory.getSplitter(ext);
                List<DocumentItemVo> itemListVo = fileHandle.handle(bytes, previewVo);

                vo.setContent(itemListVo);
                splitList.add(vo);
            }

            return splitList;

        } catch (IllegalStateException | IOException e) {
            throw new BusinessException("上传失败" + e.getMessage());
        }
    }

    /**
     * 上传文件
     * @param previewVo PreviewVo
     */
    @Override
    public void uploadFile(PreviewVo previewVo) {

        try {

            for (MultipartFile file : previewVo.getFiles()) {

                ExcelReader reader = ExcelUtil.getReader(file.getInputStream());
                // 获取所有sheet的名称列表
                List<String> sheetNames = reader.getSheetNames();
                // 遍历所有sheet并读取数据
                for (String sheetName : sheetNames) {
                    // 切换到指定的sheet
                    reader.setSheet(sheetName);
                    // 读取当前sheet的所有行数据，每行数据为一个Map对象，键为列名，值为列值
                    List<Map<String, Object>> rows = reader.readAll();
                    if (CollectionUtils.isEmpty(rows)) {
                        continue;
                    }

                    KnowledgeDocumentEntity knowledgeDocument = new KnowledgeDocumentEntity();
                    knowledgeDocument.setName(sheetName);
                    String documentId = IdUtil.randomUUID();
                    knowledgeDocument.setDocumentId(documentId);
                    knowledgeDocument.setStatus(StatusEnum.YES.getCode());
                    knowledgeDocument.setQuestionStatus(StatusEnum.YES.getCode());
                    knowledgeDocument.setActive(StatusEnum.YES.getCode());
                    knowledgeDocument.setDatasetId(previewVo.getDatasetId());
                    knowledgeDocument.setParagraphNum(rows.size());
                    knowledgeDocument.setAnswerType("model");
                    knowledgeDocument.setRedirectSimilar(0.900);
                    knowledgeDocument.setCreateTime(Tool.nowDateTime());

                    knowledgeDocumentMapper.insert(knowledgeDocument);
                    int fileSize = 0;
                    for (Map<String, Object> row : rows) {
                        StringBuilder content = new StringBuilder();
                        String title = "";
                        String question = "";

                        if (previewVo.getFileType().equals("excel")) {
                            for (Map.Entry<String, Object> entry : row.entrySet()) {
                                content.append(entry.getKey()).append(":").append(entry.getValue()).append(" ");
                            }
                        } else {
                            int i = 0;
                            for (Map.Entry<String, Object> entry : row.entrySet()) {
                                if (i == 0) {
                                    title = String.valueOf(entry.getValue());
                                } else if (i == 1) {
                                    content.append(entry.getValue());
                                } else if (i == 2) {
                                    question = String.valueOf(entry.getValue());
                                }

                                i++;
                            }
                        }

                        int byteSize = String.valueOf(content).getBytes(StandardCharsets.UTF_8).length;
                        fileSize += byteSize;

                        KnowledgeParagraphEntity paragraph = new KnowledgeParagraphEntity();
                        paragraph.setParagraphId(IdUtil.randomUUID());
                        paragraph.setTitle(title);
                        paragraph.setContent(content.toString());
                        paragraph.setDatasetId(previewVo.getDatasetId());
                        paragraph.setDocumentId(documentId);
                        paragraph.setStatus(DocumentStatusEnum.PENDING.getCode());
                        paragraph.setActive(DocumentStatusEnum.PENDING.getCode());
                        paragraph.setCreateTime(Tool.nowDateTime());

                        knowledgeParagraphMapper.insert(paragraph);

                        // 如果是QA问题
                        if (previewVo.getFileType().equals("qa") && !question.isBlank()) {
                            List<String> queationList = Arrays.stream(question.split("\n")).toList();

                            for (String questionItem : queationList) {
                                // 写入问题
                                KnowledgeQuestionEntity questionEntity = new KnowledgeQuestionEntity();
                                questionEntity.setQuestionId(IdUtil.randomUUID());
                                questionEntity.setContent(questionItem);
                                questionEntity.setHitNums(0);
                                questionEntity.setDatasetId(paragraph.getDatasetId());
                                questionEntity.setCreateTime(Tool.nowDateTime());
                                knowledgeQuestionMapper.insert(questionEntity);

                                // 写入问题关联
                                KnowledgeQuestionParagraphEntity questionParagraph = new KnowledgeQuestionParagraphEntity();
                                questionParagraph.setUuid(IdUtil.randomUUID());
                                questionParagraph.setDatasetId(paragraph.getDatasetId());
                                questionParagraph.setDocumentId(paragraph.getDocumentId());
                                questionParagraph.setParagraphId(paragraph.getParagraphId());
                                questionParagraph.setQuestionId(questionEntity.getQuestionId());
                                questionParagraph.setCreateTime(Tool.nowDateTime());
                                knowledgeQuestionParagraphMapper.insert(questionParagraph);
                            }
                        }
                    }

                    // 更新文件大小
                    KnowledgeDocumentEntity documentInfo = knowledgeDocumentMapper.selectById(documentId);
                    documentInfo.setFileSize(fileSize);
                    knowledgeDocumentMapper.updateById(documentInfo);
                }

                reader.close();
            }

        } catch (IllegalStateException | IOException e) {
            throw new BusinessException("上传失败" + e.getMessage());
        }
    }

    /**
     * 保存文档
     * @param documentSaveVo DocumentSaveVo
     */
    @Override
    @Transactional
    public void saveDocument(DocumentSaveVo documentSaveVo) {

        for (DocumentSplitVo document : documentSaveVo.getDocumentList()) {

            if (document.getContent().isEmpty()) {
                throw new BusinessException("文档内容为空");
            }

            // 写入文档
            KnowledgeDocumentEntity knowledgeDocument = new KnowledgeDocumentEntity();
            knowledgeDocument.setName(document.getName());
            String documentId = IdUtil.randomUUID();
            knowledgeDocument.setDocumentId(documentId);
            knowledgeDocument.setFileSize(document.getFileSize());
            knowledgeDocument.setStatus(StatusEnum.YES.getCode());
            knowledgeDocument.setQuestionStatus(StatusEnum.YES.getCode());
            knowledgeDocument.setActive(StatusEnum.YES.getCode());
            knowledgeDocument.setDatasetId(documentSaveVo.getDatasetId());
            knowledgeDocument.setParagraphNum(document.getContent().size());
            knowledgeDocument.setAnswerType("model");
            knowledgeDocument.setRedirectSimilar(0.900);
            knowledgeDocument.setCreateTime(Tool.nowDateTime());

            knowledgeDocumentMapper.insert(knowledgeDocument);

            // 写入段落
            for (DocumentItemVo content : document.getContent()) {

                KnowledgeParagraphEntity paragraph = new KnowledgeParagraphEntity();
                paragraph.setParagraphId(IdUtil.randomUUID());
                paragraph.setTitle(content.getTitle());
                paragraph.setContent(content.getContent());
                paragraph.setDatasetId(documentSaveVo.getDatasetId());
                paragraph.setDocumentId(documentId);
                paragraph.setStatus(DocumentStatusEnum.PENDING.getCode());
                paragraph.setActive(DocumentStatusEnum.PENDING.getCode());
                paragraph.setCreateTime(Tool.nowDateTime());

                knowledgeParagraphMapper.insert(paragraph);
            }
        }
    }

    /**
     * 向量化文本
     * @param documentIds String
     */
    @Override
    public void doEmbedding(String documentIds) {

        String[] documentMap = documentIds.split(",");
        for (String documentId : documentMap) {
            // 检测应答模式
            KnowledgeDocumentEntity documentInfo = knowledgeDocumentMapper.selectById(documentId);
            KnowledgeDatasetEntity datasetInfo = knowledgeDatasetMapper.selectById(documentInfo.getDatasetId());

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

    /**
     * 设置模型
     * @param settingVo DocumentSettingVo
     */
    @Override
    public void setDocument(DocumentSettingVo settingVo) {

        String[] documentIds = settingVo.getDocumentIds().split(",");
        for (String documentId : documentIds) {

            KnowledgeDocumentEntity documentInfo = knowledgeDocumentMapper.selectById(documentId);
            documentInfo.setAnswerType(settingVo.getAnswerType());
            documentInfo.setRedirectSimilar(settingVo.getRedirectSimilar());

            knowledgeDocumentMapper.updateById(documentInfo);
        }
    }

    /**
     * 删除文档
     * @param documentIds String
     */
    @Override
    @Transactional
    public void delDocumentByIds(String documentIds) {

        List<String> documentIdsList = Collections.singletonList(documentIds);

        // 删除文档
        knowledgeDocumentMapper.deleteByIds(documentIdsList);
        // 删除文档段落
        knowledgeParagraphMapper.deleteByDocumentIds(documentIdsList);
        // 删除文档embedding数据
        knowledgeEmbeddingMapper.deleteByDocumentIds(documentIdsList);
        // 删除文档下问题数据
        knowledgeQuestionParagraphMapper.deleteByDocumentIds(documentIdsList);
    }

    /**
     * 生成问题
     * @param questionVo QuestionVo
     */
    @Override
    public void makeQuestion(QuestionVo questionVo) {

       if (questionVo.getPrompt().isBlank()) {
            throw new BusinessException("提示词不能为空");
        }

        // 要生成问题的文档
        List<String> documentIds = Arrays.stream(questionVo.getDocumentIds().split(",")).toList();
        if (CollectionUtils.isEmpty(documentIds)) {
            throw new BusinessException("请购选文档");
        }

        List<String> modelSetData = Arrays.stream(questionVo.getModelId().split(",")).toList();

        // 构建模型
        ModelsEntity modelInfo = modelsMapper.selectById(modelSetData.get(0));
        if (modelInfo == null) {
            throw new BusinessException("模型异常");
        }

        task.executeAsyncQuestionTask(modelSetData, modelInfo, documentIds, questionVo);
    }
}
