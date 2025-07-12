// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.common.utils;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * embedding拆分文本
 */
@Component
public class MarkChunk implements IChunkInterface {
    private static final int MAX_CHUNK_LEN = 256;
    private static final String SPLIT_CHUNK_PATTERN = String.format(".{1,%d}[。| |\\\\.|！|;|；|!|\\n]", MAX_CHUNK_LEN);
    private static final String MAX_CHUNK_PATTERN = String.format(".{1,%d}", MAX_CHUNK_LEN);

    @Override
    public List<String> handle(String chunk) {
        Pattern splitPattern = Pattern.compile(SPLIT_CHUNK_PATTERN, Pattern.DOTALL);
        Pattern maxPattern = Pattern.compile(MAX_CHUNK_PATTERN, Pattern.DOTALL);

        // 处理通过split_chunk_pattern匹配的部分
        List<String> chunkResults = new ArrayList<>();
        Matcher splitMatcher = splitPattern.matcher(chunk);
        while (splitMatcher.find()) {
            String matched = splitMatcher.group();
            if (!matched.trim().isEmpty()) {
                chunkResults.add(matched.trim());
            }
        }

        // 处理剩余未匹配部分
        String[] otherChunks = splitPattern.split(chunk);
        for (String otherChunk : otherChunks) {
            if (otherChunk.isEmpty()) continue;

            if (otherChunk.length() < MAX_CHUNK_LEN) {
                String trimmed = otherChunk.trim();
                if (!trimmed.isEmpty()) {
                    chunkResults.add(trimmed);
                }
            } else {
                Matcher maxMatcher = maxPattern.matcher(otherChunk);
                while (maxMatcher.find()) {
                    String matched = maxMatcher.group();
                    if (!matched.trim().isEmpty()) {
                        chunkResults.add(matched.trim());
                    }
                }
            }
        }

        return chunkResults;
    }
}
