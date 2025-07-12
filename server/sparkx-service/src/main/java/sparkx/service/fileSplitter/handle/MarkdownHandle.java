// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.service.fileSplitter.handle;

import com.vladsch.flexmark.util.ast.Node;
import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import sparkx.service.fileSplitter.FileHandleInterface;
import sparkx.service.vo.document.DocumentItemVo;
import sparkx.service.vo.document.PreviewVo;

/**
 * 解析markdown
 */
public class MarkdownHandle implements FileHandleInterface {

    @Override
    public List<DocumentItemVo> handle(byte[] bytes, PreviewVo previewVo) {

        List<DocumentItemVo> itemListVo = new LinkedList<>();
        List<MarkdownHandle.Section> markDownList = MarkdownHandle.parseMarkdown(new String(bytes));
        markDownList.forEach(item -> {
            DocumentItemVo itemVo = new DocumentItemVo();
            itemVo.setTitle(item.getTitle());
            itemVo.setContent(item.getContent());

            itemListVo.add(itemVo);
        });

        return itemListVo;
    }

    @Data
    public static class Section {
        private String title;
        private String content;
    }

    public static List<Section> parseMarkdown(String markdown) {
        Parser parser = Parser.builder().build();
        Document document = (Document) parser.parse(markdown);
        List<Section> sections = new ArrayList<>();
        Node currentNode = document.getFirstChild();
        StringBuilder currentContent = new StringBuilder();
        Section currentSection = null;

        while (currentNode != null) {
            if (currentNode instanceof Heading) {
                // 遇到新标题时保存前一个章节
                if (currentSection != null) {
                    currentSection.setContent(currentContent.toString().trim());
                    sections.add(currentSection);
                }
                // 创建新章节
                currentSection = new Section();
                currentSection.setTitle(((Heading) currentNode).getText().toString());
                currentContent = new StringBuilder();
            } else if (currentSection != null) {
                // 收集非标题内容
                currentContent.append(BasedSequence.of(currentNode.getChars())).append("\n");
            }
            currentNode = currentNode.getNext();
        }
        // 处理最后一个章节
        if (currentSection != null) {
            currentSection.setContent(currentContent.toString().trim());
            sections.add(currentSection);
        }
        return sections;
    }
}