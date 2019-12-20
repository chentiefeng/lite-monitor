package me.ctf.lm.util;

import com.google.gson.Gson;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chentiefeng
 */
@Data
public class DingSendResult {
    private Gson gson = new Gson();
    private boolean isSuccess;
    private Integer errorCode;
    private String errorMsg;

    @Override
    public String toString() {
        Map<String, Object> items = new HashMap<>(8);
        items.put("errorCode", errorCode);
        items.put("errorMsg", errorMsg);
        items.put("isSuccess", isSuccess);
        return gson.toJson(items);
    }
}
