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

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateRange;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

public class Tool {

    /**
     * 生成密码
     * @param password String
     * @param salt String
     * @return String
     */
    public static String makePassword(String password, String salt) {
        return SecureUtil.md5(SecureUtil.md5(password + salt) + salt);
    }

    /**
     * 验证密码
     * @param dbPassword String
     * @param password String
     * @param salt String
     * @return String
     */
    public static boolean verifyPassword(String dbPassword, String password, String salt) {
        return Objects.equals(dbPassword, makePassword(password, salt));
    }

    /**
     * 获取当前时间
     * @return LocalDateTime
     */
    public static LocalDateTime nowDateTime() {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse((new DateTime()).toString(), df);
    }

    /**
     * 给定的时间转格式
     * @param time String
     * @ LocalDateTime
     */
    public static LocalDateTime time2LocalDateTime(String time) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(time, df);
    }

    /**
     * 清理文本杂质
     * @param input String
     * @return String
     */
    public static String cleanText(String input) {
        String result = input;
        for (int i = 0; i < PATTERNS.length; i++) {
            result = PATTERNS[i].matcher(result).replaceAll(REPLACEMENTS[i]);
        }
        return result;
    }

    private static final Pattern[] PATTERNS = {
            Pattern.compile("\\n+"),  // 合并多个换行
            Pattern.compile(" +"),    // 合并多个空格
            Pattern.compile("#+"),    // 移除所有井号
            Pattern.compile("\\t+")   // 移除所有制表符
    };

    private static final String[] REPLACEMENTS = {
            "\n", " ", "", ""
    };

    /**
     * 获取时间范围
     * @param startDate String
     * @param endDate String
     * @return List<String>
     */
    public static List<String> getDateRange(String startDate, String endDate) {

        DateTime start = DateUtil.parse(startDate);
        DateTime end = DateUtil.parse(endDate);

        DateRange range = DateUtil.range(start, end, DateField.DAY_OF_MONTH);
        List<String> dayRange = new LinkedList<>();
        range.forEach(item -> {
            dayRange.add(DateUtil.format(item, "yyyy-MM-dd"));
        });

        return dayRange;
    }

    /**
     * 构建发送方法
     * @param runtimeId long
     * @param nodeId String
     * @param content String
     * @return String
     */
    public static String buildSendData(long runtimeId, String nodeId, String content) {

        Map<String, String> returnData = new HashMap<>();
        returnData.put("runtimeId", String.valueOf(runtimeId));
        returnData.put("content", content);
        returnData.put("nodeId", nodeId);

        return JSONUtil.toJsonStr(returnData);
    }

    /**
     * 生成token
     * @return String
     */
    public static String makeToken() {
        long id = IdUtil.getSnowflake(1, 1).nextId();
        return Long.toHexString(id).substring(0, 16).toUpperCase();
    }
}
