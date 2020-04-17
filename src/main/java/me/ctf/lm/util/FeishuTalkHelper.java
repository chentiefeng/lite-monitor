package me.ctf.lm.util;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chentiefeng
 * @date 2019/02/18 17:57
 */
@Slf4j
public class FeishuTalkHelper {
    private static final RestTemplate REST_TEMPLATE = new RestTemplateBuilder().build();
    private static final Gson GSON = new Gson();

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
        Map<String, Object> map = new HashMap<>(8);
        map.put("title", title);
        map.put("text", content);
        HttpEntity<String> r = new HttpEntity<>(GSON.toJson(map), headers);
        REST_TEMPLATE.postForObject("https://open.feishu.cn/open-apis/bot/hook/" + token, r, String.class);
    }

}
