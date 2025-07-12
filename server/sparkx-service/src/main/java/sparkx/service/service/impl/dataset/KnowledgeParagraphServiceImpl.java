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
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sparkx.common.core.PageResult;
import sparkx.common.enums.StatusEnum;
import sparkx.common.exception.BusinessException;
import sparkx.common.utils.Tool;
import sparkx.service.entity.dataset.KnowledgeDocumentEntity;
import sparkx.service.entity.dataset.KnowledgeEmbeddingEntity;
import sparkx.service.entity.dataset.KnowledgeParagraphEntity;
import sparkx.service.entity.dataset.KnowledgeQuestionParagraphEntity;
import sparkx.service.mapper.dataset.KnowledgeDocumentMapper;
import sparkx.service.mapper.dataset.KnowledgeEmbeddingMapper;
import sparkx.service.mapper.dataset.KnowledgeParagraphMapper;
import sparkx.service.mapper.dataset.KnowledgeQuestionParagraphMapper;
import sparkx.service.service.interfaces.dataset.IKnowledgeParagraphService;
import sparkx.service.task.EmbeddingDocumentTask;
import sparkx.service.vo.paragraph.ParagraphAddVo;
import sparkx.service.vo.paragraph.ParagraphListVo;
import sparkx.service.vo.paragraph.ParagraphQueryVo;
import sparkx.service.vo.paragraph.ParagraphVo;

import java.util.LinkedList;
import java.util.List;

@Service
public class KnowledgeParagraphServiceImpl implements IKnowledgeParagraphService {

    @Autowired
    KnowledgeParagraphMapper knowledgeParagraphMapper;

    @Autowired
    KnowledgeDocumentMapper knowledgeDocumentMapper;

    @Autowired
    KnowledgeEmbeddingMapper knowledgeEmbeddingMapper;

    @Autowired
    KnowledgeQuestionParagraphMapper knowledgeQuestionParagraphMapper;

    @Autowired
    EmbeddingDocumentTask task;

    /**
     * 段落列表
     * @param queryVo ParagraphQueryVo
     * @return PageResult<ParagraphListVo>
     */
    @Override
    public PageResult<ParagraphListVo> getParagraphList(ParagraphQueryVo queryVo) {

        long pageNo   = queryVo.getPage();
        long pageSize = queryVo.getLimit();

        QueryWrapper<KnowledgeParagraphEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("document_id", queryVo.getDocumentId());
        queryWrapper.orderByDesc("create_time");

        IPage<KnowledgeParagraphEntity> paragraphListRes =
                knowledgeParagraphMapper.selectPage(new Page<>(pageNo, pageSize), queryWrapper);

        List<ParagraphListVo> paragraphListVoList = new LinkedList<>();
        for (KnowledgeParagraphEntity entity : paragraphListRes.getRecords()) {
            ParagraphListVo vo = new ParagraphListVo();
            BeanUtils.copyProperties(entity, vo);

            paragraphListVoList.add(vo);
        }

        return PageResult.iPageHandle(paragraphListRes.getTotal(), pageNo, pageSize, paragraphListVoList);
    }

    /**
     * 激活、关闭段落
     * @param paragraphVo ParagraphVo
     */
    @Override
    public void activeParagraph(ParagraphVo paragraphVo) {

        KnowledgeParagraphEntity paragraph = knowledgeParagraphMapper.selectById(paragraphVo.getParagraphId());
        paragraph.setActive(paragraphVo.getActive());
        paragraph.setUpdateTime(Tool.nowDateTime());

        knowledgeParagraphMapper.updateById(paragraph);
    }

    /**
     * 编辑段落
     *
     * @param paragraphVo ParagraphVo
     */
    @Override
    public void editParagraph(ParagraphVo paragraphVo) {

        if (paragraphVo.getContent().isBlank()) {
            throw new BusinessException("内容不允许为空");
        }

        KnowledgeParagraphEntity paragraph = knowledgeParagraphMapper.selectById(paragraphVo.getParagraphId());
        boolean editFlag = false;
        // 是否做了修改
        if (!paragraph.getTitle().equals(paragraphVo.getTitle()) ||
                !paragraph.getContent().equals(paragraphVo.getContent())) {
            editFlag = true;
        }

        if (editFlag) {
            // 编辑内容
            paragraph.setTitle(paragraphVo.getTitle());
            paragraph.setContent(paragraphVo.getContent());
            paragraph.setUpdateTime(Tool.nowDateTime());

            knowledgeParagraphMapper.updateById(paragraph);

            // 检测应答模式
            KnowledgeDocumentEntity documentInfo = knowledgeDocumentMapper.selectById(paragraph.getDocumentId());
            if (documentInfo.getAnswerType().equals("model")) {
                // 执行向量化
                task.executeAsyncParagraphTask(paragraph.getParagraphId());
            }
        }
    }

    /**
     * 删除段落
     * @param paragraphVo ParagraphVo
     */
    @Override
    @Transactional
    public void delParagraph(ParagraphVo paragraphVo) {

        KnowledgeParagraphEntity info = knowledgeParagraphMapper.selectById(paragraphVo.getParagraphId());
        // 段落表删除
        knowledgeParagraphMapper.deleteById(paragraphVo.getParagraphId());

        // 文档表段落 -1
        KnowledgeDocumentEntity documentInfo = knowledgeDocumentMapper.selectById(info.getDocumentId());
        documentInfo.setParagraphNum(documentInfo.getParagraphNum() - 1);

        knowledgeDocumentMapper.updateById(documentInfo);

        // 向量表属于这个段落的全删
        knowledgeEmbeddingMapper.delete(new QueryWrapper<KnowledgeEmbeddingEntity>()
                .eq("paragraph_id", paragraphVo.getParagraphId()));

        // 删掉问题关联的段落
        knowledgeQuestionParagraphMapper.delete(new QueryWrapper<KnowledgeQuestionParagraphEntity>()
                .eq("paragraph_id", paragraphVo.getParagraphId()));
    }

    /**
     * 添加段落
     * @param paragraphAddVo ParagraphAddVo
     */
    @Override
    public void addParagraph(ParagraphAddVo paragraphAddVo) {

        if (paragraphAddVo.getContent().isBlank()) {
            throw new BusinessException("内容不允许为空");
        }

        // 添加段落
        KnowledgeParagraphEntity paragraph = new KnowledgeParagraphEntity();
        paragraph.setParagraphId(IdUtil.randomUUID());
        paragraph.setTitle(paragraphAddVo.getTitle());
        paragraph.setContent(paragraphAddVo.getContent());
        paragraph.setDatasetId(paragraphAddVo.getDatasetId());
        paragraph.setDocumentId(paragraphAddVo.getDocumentId());
        paragraph.setActive(StatusEnum.YES.getCode());
        paragraph.setCreateTime(Tool.nowDateTime());

        knowledgeParagraphMapper.insert(paragraph);

        // 检测应答模式
        KnowledgeDocumentEntity documentInfo = knowledgeDocumentMapper.selectById(paragraph.getDocumentId());
        if (documentInfo.getAnswerType().equals("model")) {
            // 执行向量化
            task.executeAsyncParagraphTask(paragraph.getParagraphId());
        }

        // 文档内容改变
        documentInfo.setParagraphNum(documentInfo.getParagraphNum() + 1);
        documentInfo.setFileSize(paragraphAddVo.getContent().length() + documentInfo.getFileSize());
        documentInfo.setUpdateTime(Tool.nowDateTime());

        knowledgeDocumentMapper.updateById(documentInfo);
    }
}
