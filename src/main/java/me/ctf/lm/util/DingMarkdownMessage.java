package me.ctf.lm.util;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chentiefeng
 * @date 2019/02/19 10:01
 */
public class DingMarkdownMessage {
    private Gson gson = new Gson();
    private String title;

    private List<String> items = new ArrayList<>();

    private String[] atMobiles;
    private Boolean atAll;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void add(String text) {
        items.add(text);
    }

    public static String getBoldText(String text) {
        return "**" + text + "**";
    }

    public static String getItalicText(String text) {
        return "*" + text + "*";
    }

    public static String getLinkText(String text, String href) {
        return "[" + text + "](" + href + ")";
    }

    public static String getImageText(String imageUrl) {
        return "![image](" + imageUrl + ")";
    }

    public static String getHeaderText(int headerType, String text) {
        if (headerType < 1 || headerType > 6) {
            throw new IllegalArgumentException("headerType should be in [1, 6]");
        }

        StringBuffer numbers = new StringBuffer();
        for (int i = 0; i < headerType; i++) {
            numbers.append("#");
        }
        return numbers + " " + text;
    }

    public static String getReferenceText(String text) {
        return "> " + text;
    }

    public static String getOrderListText(List<String> orderItem) {
        if (orderItem.isEmpty()) {
            return "";
        }

        StringBuffer sb = new StringBuffer();
        for (int i = 1; i <= orderItem.size() - 1; i++) {
            sb.append(String.valueOf(i) + ". " + orderItem.get(i - 1) + "\n");
        }
        sb.append(String.valueOf(orderItem.size()) + ". " + orderItem.get(orderItem.size() - 1));
        return sb.toString();
    }

    public static String getUnorderListText(List<String> unorderItem) {
        if (unorderItem.isEmpty()) {
            return "";
        }

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < unorderItem.size() - 1; i++) {
            sb.append("- " + unorderItem.get(i) + "\n");
        }
        sb.append("- " + unorderItem.get(unorderItem.size() - 1));
        return sb.toString();
    }

    public void setAtMobiles(String[] atMobiles) {
        this.atMobiles = atMobiles;
    }

    public void setAtAll(Boolean atAll) {
        this.atAll = atAll;
    }

    public String toJsonString() {
        Map<String, Object> result = new HashMap<>(8);
        result.put("msgtype", "markdown");

        Map<String, Object> markdown = new HashMap<>(8);
        markdown.put("title", title);

        StringBuilder markdownText = new StringBuilder();
        for (String item : items) {
            markdownText.append(item).append("\n");
        }
        markdown.put("text", markdownText.toString());
        result.put("markdown", markdown);
        Map<String, Object> at = new HashMap<>(8);
        if (atMobiles != null && atMobiles.length > 0) {
            at.put("atMobiles", atMobiles);
        }
        at.put("isAtAll", atAll);
        result.put("at", at);
        return gson.toJson(result);
    }

}
