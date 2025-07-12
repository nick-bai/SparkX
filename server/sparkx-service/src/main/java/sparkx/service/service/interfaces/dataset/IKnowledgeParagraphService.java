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
import sparkx.service.vo.paragraph.ParagraphAddVo;
import sparkx.service.vo.paragraph.ParagraphListVo;
import sparkx.service.vo.paragraph.ParagraphQueryVo;
import sparkx.service.vo.paragraph.ParagraphVo;

/**
 * <p>
 * 文档段落表 服务类
 * </p>
 *
 * @author NickBai
 * @since 2025-03-05
 */
public interface IKnowledgeParagraphService {

    /**
     * 段落列表
     * @param queryVo ParagraphQueryVo
     * @return PageResult<ParagraphListVo>
     */
    PageResult<ParagraphListVo> getParagraphList(ParagraphQueryVo queryVo);

    /**
     * 激活、关闭段落
     * @param paragraphVo ParagraphVo
     */
    void activeParagraph(ParagraphVo paragraphVo);

    /**
     * 编辑段落
     * @param paragraphVo ParagraphVo
     */
    void editParagraph(ParagraphVo paragraphVo);

    /**
     * 删除段落
     * @param paragraphVo ParagraphVo
     */
    void delParagraph(ParagraphVo paragraphVo);

    /**
     * 添加段落
     * @param paragraphAddVo ParagraphAddVo
     */
    void addParagraph(ParagraphAddVo paragraphAddVo);
}
