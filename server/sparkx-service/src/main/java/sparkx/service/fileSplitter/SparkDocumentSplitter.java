// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.service.fileSplitter;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.document.splitter.DocumentByRegexSplitter;
import dev.langchain4j.data.segment.TextSegment;
import lombok.extern.slf4j.Slf4j;
import sparkx.common.utils.Tool;
import sparkx.service.vo.document.DocumentItemVo;
import sparkx.service.vo.document.PreviewVo;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

@Slf4j
public class SparkDocumentSplitter {

    /**
     * 执行文本拆分
     * @param parser DocumentParser
     * @param inputStream InputStream
     * @param previewVo PreviewVo
     * @return List<DocumentItemVo>
     */
    public static List<DocumentItemVo> splitter(DocumentParser parser, InputStream inputStream, PreviewVo previewVo) {

        try {

            // 文本解析器
            Document document = parser.parse(inputStream);
            DocumentSplitter splitter;
            // 如果是自定义拆分
            if (previewVo.getSplitType().equals(2) && !previewVo.getPattern().isBlank()) {

                DocumentSplitter subSplitter = new DocumentByParagraphSplitter(previewVo.getSplitLen(), 10);
                splitter = new DocumentByRegexSplitter("[" + previewVo.getPattern() + "]", "\n", previewVo.getSplitLen(), 10, subSplitter);
            } else {
                // 512个字符 10个重合度拆分文本
                splitter = new DocumentByParagraphSplitter(previewVo.getSplitLen(), 10);
            }

            List<TextSegment> segments = splitter.split(document);

            List<DocumentItemVo> itemListVo = new LinkedList<>();
            segments.forEach(segment -> {
                DocumentItemVo itemVo = new DocumentItemVo();
                itemVo.setTitle("");

                // 自动清理
                String content = segment.text();
                if (previewVo.getAutoClean().equals(1)) {
                    content = Tool.cleanText(content);
                }
                itemVo.setContent(content);

                itemListVo.add(itemVo);
            });

            return itemListVo;
        } catch (Exception e) {
            log.error("解析文本 {}, 出错: {}", previewVo, e.getMessage());
            return new LinkedList<>();
        }
    }
}
