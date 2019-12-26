package me.ctf.lm.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 占位符工具类
 *
 * @author chentiefeng
 * @date 2019/12/26 08:30
 */
public class PlaceholderUtil {

    private static final Pattern PATTERN = Pattern.compile("\\$\\{[^}]+}");

    /**
     * 解析占位符变量<br/>
     * <br/>
     * 占位符格式<code>${abc}</code><br/>
     * <i>例子1：<code>s = hello ${abc}，analyze(s,{abc=world}) = hello world</code></i><br/>
     * <i>例子2：<code>s = hello ${abc}，analyze(s,{ab=world}) = hello ${abc}</code></i><br/>
     * <br/>
     * <strong>日期占位符格式：${yyyyMMdd,addAmount,unit}</strong><br/>
     * <i>例子1：<code>s = hello ${yyyyMMdd}，analyze(s,null) = hello 20191226</code></i><br/>
     * <i>例子2：<code>s = hello ${yyyyMMdd,-1}，analyze(s,null) = hello 20191225</code></i><br/>
     * <i>例子3：<code>s = hello ${yyyyMMdd,-1,Months}，analyze(s,null) = hello 20191126</code></i><br/>
     * <pre>日期格式和单位请参考java.time.format.DateTimeFormatter和java.time.temporal.ChronoUnit</pre>
     *
     * @param varString
     * @param varMap
     * @return 解析后的字符串
     * @see DateTimeFormatter
     * @see ChronoUnit
     */
    public static String analyze(String varString, Map<String, Object> varMap) {
        return analyze(varString, varMap, LocalDateTime.now(), null);
    }

    /**
     * 解析参数变量
     *
     * @param varString
     * @param varMap
     * @param now
     * @param specialString
     * @return
     */
    private static String analyze(String varString, Map<String, Object> varMap, LocalDateTime now, String specialString) {
        if (varString == null || varString.trim().length() == 0) {
            return varString;
        }
        StringBuffer sb = new StringBuffer();
        Matcher matcher = PATTERN.matcher(varString);
        String group, var;
        while (matcher.find() && (group = matcher.group()) != null) {
            var = group.substring(2, group.length() - 1);
            Object value;
            if ((value = specialString) != null || (value = getVarValue(var, varMap, now)) != null) {
                matcher.appendReplacement(sb, value.toString());
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 获取变量值
     *
     * @param var
     * @param varMap
     * @param now
     * @return
     */
    private static Object getVarValue(String var, Map<String, Object> varMap, LocalDateTime now) {
        Object value;
        if (varMap != null && (value = varMap.get(var)) != null) {
            //普通变量
            return value;
        }
        if (var != null && now != null) {
            String[] arr = var.split(",");
            LocalDateTime next;
            DateTimeFormatter dtf;
            try {
                dtf = DateTimeFormatter.ofPattern(arr[0]);
            } catch (IllegalArgumentException e) {
                return null;
            }
            switch (arr.length) {
                case 2:
                    //时间变量有加减，默认加减天,${yyyyMMdd,2}
                    next = getNextDate(now, arr[1].trim(), ChronoUnit.DAYS);
                    break;
                case 3:
                    //时间变量有加减，默认加减天,${yyyyMMdd,2,Months}
                    next = getNextDate(now, arr[1].trim(), ChronoUnit.valueOf(arr[2].trim().toUpperCase()));
                    break;
                default:
                    //时间变量，无加减,${yyyyMMdd}
                    next = now;
                    break;
            }
            return dtf.format(next);
        }
        return null;
    }

    /**
     * 所有变量替换成""
     *
     * @param varString
     * @return
     */
    public static String analyzeToBlank(String varString) {
        return analyze(varString, null, null, "");
    }

    /**
     * 用固定字符串解析
     *
     * @param varString
     * @param s
     * @return
     */
    public static String analyzeToString(String varString, String s) {
        return analyze(varString, null, null, s);
    }

    /**
     * 所有变量替换成''
     *
     * @param varString
     * @return
     */
    public static String analyzeToSingleBlank(String varString) {
        return analyze(varString, null, null, "''");
    }

    /**
     * 获取时间
     *
     * @param now
     * @param express
     * @param unit
     * @return
     */
    private static LocalDateTime getNextDate(LocalDateTime now, String express, ChronoUnit unit) {
        int amount = express.startsWith("+") ? Integer.parseInt(express.substring(1)) : Integer.parseInt(express);
        return now.plus(amount, unit);
    }

    public static void main(String[] args) {
        String s = "hello ${yyyyMMdd}";
        Map<String, Object> map = new HashMap<>();
        map.put("ab", "world");
        System.out.println(analyze(s, null));
    }
}
