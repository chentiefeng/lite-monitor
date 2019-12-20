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
public class DingTalkHelper {
    private static RestTemplate restTemplate = new RestTemplateBuilder().build();

    /**
     * text钉钉消息发送
     *
     * @param msg    消息
     * @param dingId 机器人id
     */
    public static void sendTextMsg(String msg, String dingId) {
        DingTalkHelper.sendTextMsg(msg, dingId, false);
    }

    /**
     * text钉钉消息发送
     *
     * @param msg    消息
     * @param dingId 机器人id
     */
    public static void sendTextMsg(String msg, String dingId, boolean isAtAll) {
        DingTextMessage dingTextMessage = new DingTextMessage(msg);
        dingTextMessage.setAtAll(isAtAll);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");
        HttpEntity<String> r = new HttpEntity<>(dingTextMessage.toJsonString(), headers);
        restTemplate.postForObject("https://oapi.dingtalk.com/robot/send?access_token=" + dingId, r, DingSendResult.class);
    }

    /**
     * 发送钉钉markdown消息
     *
     * @param message
     * @param dingId
     */
    public static void sendMarkdownMsg(DingMarkdownMessage message, String dingId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");
        HttpEntity<String> r = new HttpEntity<>(message.toJsonString(), headers);
        restTemplate.postForObject("https://oapi.dingtalk.com/robot/send?access_token=" + dingId, r, DingSendResult.class);
        log.info(message.toJsonString());
    }
}
