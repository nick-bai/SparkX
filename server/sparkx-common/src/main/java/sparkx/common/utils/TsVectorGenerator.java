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

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.CustomDictionary;
import com.hankcs.hanlp.seg.common.Term;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 全文索引拆词
 */
@Component
public class TsVectorGenerator {
    private static final String[] JIEBA_WORD_LIST_CACHE = generateCharCache();
    private static final List<Pattern> WORD_PATTERNS = Arrays.asList(
            Pattern.compile("v\\d+\\.\\d+\\.\\d+"),
            Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}")
    );

    private static String[] generateCharCache() {
        List<String> chars = new ArrayList<>();
        for (int i = 38; i < 84; i++) {
            chars.add(Character.toString((char) i));
        }
        return chars.toArray(new String[0]);
    }

    // 主转换方法
    public static String toTsVector(String text) {

        List<WordPosition> wordPositions = buildWordPositions(text);
        // 6. 构建最终结果
        return buildTsVector(wordPositions);
    }

    public static String toTsQuery(String text) {

        List<WordPosition> wordPositions = buildWordPositions(text);
        // 6. 构建最终结果
        return buildTsQuery(wordPositions);
    }

    private static List<WordPosition> buildWordPositions(String text) {
        // 1. 提取特殊词汇
        List<String> specialWords = extractSpecialWords(text);

        // 2. 创建替换字典
        Map<String, String> wordMap = createWordMap(specialWords, text);

        // 3. 替换文本中的特殊词汇
        String processedText = replaceSpecialWords(text, wordMap);

        // 4. 分词处理
        List<Term> terms = HanLP.segment(processedText);

        // 5. 过滤和处理分词结果
        return processTerms(terms, wordMap);
    }

    private static List<String> extractSpecialWords(String text) {
        List<String> words = new ArrayList<>();
        for (Pattern pattern : WORD_PATTERNS) {
            Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                String[] split = matcher.group().split(":");
                Collections.addAll(words, split);
            }
        }
        return words;
    }

    private static Map<String, String> createWordMap(List<String> specialWords, String originalText) {
        Map<String, String> map = new HashMap<>();
        Set<String> usedMarkers = new HashSet<>();

        for (String word : specialWords) {
            String marker = findAvailableMarker(originalText, usedMarkers);
            map.put("#" + marker + "#", word);
            CustomDictionary.add("#" + marker + "#");
        }
        return map;
    }

    private static String findAvailableMarker(String text, Set<String> used) {
        for (String c : JIEBA_WORD_LIST_CACHE) {
            if (!text.contains(c) && !used.contains(c)) {
                used.add(c);
                return c;
            }
        }
        return UUID.randomUUID().toString().replace("-", "");
    }

    private static String replaceSpecialWords(String text, Map<String, String> wordMap) {
        String result = text;
        for (Map.Entry<String, String> entry : wordMap.entrySet()) {
            String pattern = "(?<!#)" + Pattern.quote(entry.getValue()) + "(?!#)";
            result = result.replaceAll(pattern, entry.getKey());
        }
        return result;
    }

    private static List<WordPosition> processTerms(List<Term> terms, Map<String, String> wordMap) {
        List<WordPosition> positions = new ArrayList<>();
        int index = 1;

        for (Term term : terms) {
            String word = wordMap.getOrDefault(term.word, term.word);
            if (shouldKeepWord(word, term.nature.toString())) {
                positions.add(new WordPosition(word.toLowerCase(), index));
            }
            index++;
        }
        return positions;
    }

    private static boolean shouldKeepWord(String word, String pos) {
        return !pos.matches("[xw]") &&
                word.length() < 10 &&
                !word.matches("[\n\\s,:\\'<>!@#$%^&*（）：；，./\"]");
    }

    private static String buildTsVector(List<WordPosition> positions) {
        Map<String, List<Integer>> positionMap = new HashMap<>();

        for (WordPosition wp : positions) {
            positionMap.computeIfAbsent(wp.word, k -> new ArrayList<>())
                    .add(wp.position);
        }

        return positionMap.entrySet().stream()
                .map(entry -> {
                    String positionsStr = entry.getValue().stream()
                            .limit(20)
                            .map(Object::toString)
                            .collect(Collectors.joining(","));
                    return entry.getKey() + ":" + positionsStr;
                })
                .collect(Collectors.joining(" "));
    }

    private static String buildTsQuery(List<WordPosition> positions) {
        Map<String, List<Integer>> positionMap = new HashMap<>();

        for (WordPosition wp : positions) {
            positionMap.computeIfAbsent(wp.word, k -> new ArrayList<>())
                    .add(wp.position);
        }

        return positionMap.entrySet().stream()
                .map(entry -> {
                    String positionsStr = entry.getValue().stream()
                            .limit(20)
                            .map(Object::toString)
                            .collect(Collectors.joining(","));
                    return entry.getKey();
                })
                .collect(Collectors.joining(" "));
    }

    // 辅助类
    private static class WordPosition {
        String word;
        int position;

        WordPosition(String word, int position) {
            this.word = word;
            this.position = position;
        }
    }
}
