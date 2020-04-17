package me.ctf.lm.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

/**
 * @author chentiefeng
 * @date 2019/02/18 17:57
 */
@Slf4j
public class FeishuTalkHelper {
    private static final RestTemplate REST_TEMPLATE = new RestTemplateBuilder().build();


    /**
     * 飞书消息发送
     *
     * @param title
     * @param content
     * @param token
     */
    public static void sendTextMsg(String title, String content, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");
        HttpEntity<String> r = new HttpEntity<>("{\"title\":" + title + ",\"text\":" + content + "}", headers);
        REST_TEMPLATE.postForObject("https://oapi.dingtalk.com/robot/send?access_token=" + token, r, String.class);
    }

}
