package me.ctf.lm.util;

import com.google.gson.Gson;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chentiefeng
 * @date 2019/02/18 16:01
 */
@Data
public class DingTextMessage {
    private Gson gson = new Gson();
    private String text;
    private List<String> atMobiles;
    private boolean isAtAll;

    public DingTextMessage(String text) {
        this.text = text;
    }


    public String toJsonString() {
        Map<String, Object> items = new HashMap<>(8);
        items.put("msgtype", "text");

        Map<String, String> textContent = new HashMap<>(8);
        if (StringUtils.isBlank(text)) {
            throw new IllegalArgumentException("text should not be blank");
        }
        textContent.put("content", text);
        items.put("text", textContent);

        Map<String, Object> atItems = new HashMap<>(8);
        if (atMobiles != null && !atMobiles.isEmpty()) {
            atItems.put("atMobiles", atMobiles);
        }
        if (isAtAll) {
            atItems.put("isAtAll", isAtAll);
        }
        items.put("at", atItems);

        return gson.toJson(items);
    }

}
