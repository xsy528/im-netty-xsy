package cn.gyyx.im.utils;

import org.springframework.web.util.HtmlUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zxw
 * @Description: 通用工具类
 * @create 2024/5/30/003018:38
 * @Version 1.0
 **/
public class CommonUtil {

    private static final String PATTERN_EVAL = "eval\\((.*)\\)";
    private static final String PATTERN_KEY_WORDS ="(on\\w+\\s*=.*?)|(java|script|ajax|\\.post|\\.get|frame|write|document|\\.js|\\$|jquery|alert|console)";
    /**
      * 清除xss数据
      * @author: zxw
      * @date: 2024/5/30/0030 18:39
      * @param value
      * @return java.lang.String
      **/
    public static String cleanXSS(String value) {
        String valueReplace=value;
        Matcher evalMatcher= Pattern.compile(PATTERN_EVAL, Pattern.CASE_INSENSITIVE).matcher(valueReplace);
        while(evalMatcher.find()) {
            valueReplace=evalMatcher.replaceAll("");
        }
        Matcher scriptMatcher=Pattern.compile(PATTERN_KEY_WORDS, Pattern.CASE_INSENSITIVE).matcher(valueReplace);
        while(scriptMatcher.find()) {
            valueReplace=scriptMatcher.replaceAll("\"\"");
        }
        //html解码um编辑器进行了特殊字符编码
        valueReplace= HtmlUtils.htmlUnescape(valueReplace);
        //html编码
        valueReplace = HtmlUtils.htmlEscape(valueReplace);
        return valueReplace;
    }
}
