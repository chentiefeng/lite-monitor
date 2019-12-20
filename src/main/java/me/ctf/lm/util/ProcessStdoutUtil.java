package me.ctf.lm.util;

import ch.ethz.ssh2.StreamGobbler;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * @author chentiefeng
 * @date 2019/02/18 14:55
 */
@Slf4j
public class ProcessStdoutUtil {
    /**
     * 解析脚本执行返回的结果集
     *  
     *
     * @param in 输入流对象
     * @return 以纯文本的格式返回
     */
    public static String processStdout(InputStream in) {
        InputStream stdout = new StreamGobbler(in);
        StringBuilder buffer = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(stdout, StandardCharsets.UTF_8));
            String line;
            while ((line = br.readLine()) != null) {
                buffer.append(line).append("\n");
            }
            br.close();
        } catch (IOException e) {
            log.error("解析脚本出错，" + e.getMessage(), e);
        }
        return buffer.toString();
    }
}
