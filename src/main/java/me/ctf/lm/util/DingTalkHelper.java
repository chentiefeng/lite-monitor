package me.ctf.lm.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author chentiefeng
 * @date 2019/02/18 17:57
 */
@Slf4j
public class DingTalkHelper {
    private static final RestTemplate REST_TEMPLATE = new RestTemplateBuilder().build();

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
        REST_TEMPLATE.postForObject("https://oapi.dingtalk.com/robot/send?access_token=" + dingId, r, DingSendResult.class);
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
        REST_TEMPLATE.postForObject("https://oapi.dingtalk.com/robot/send?access_token=" + dingId, r, DingSendResult.class);
        log.info(message.toJsonString());
    }

    /**
     * 发送钉钉markdown消息
     *
     * @param message
     * @param dingId
     */
    public static void sendMarkdownMsg(DingMarkdownMessage message, String dingId, long timestamp, String sign) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");
        HttpEntity<String> r = new HttpEntity<>(message.toJsonString(), headers);
        REST_TEMPLATE.postForObject("https://oapi.dingtalk.com/robot/send?access_token=" + dingId + "&timestamp=" + timestamp + "&sign=" + sign, r, DingSendResult.class);
        log.info(message.toJsonString());
    }

    public static String sign(Long timestamp, String secret) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
        String stringToSign = timestamp + "\n" + secret;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
        return URLEncoder.encode(new String(Base64.encodeBase64(signData)), "UTF-8");
    }
}
