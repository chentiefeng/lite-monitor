package me.ctf.lm.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: chentiefeng[chentiefeng@linzikg.com]
 * @create: 2019-12-12 14:38
 */
public class MapResult extends HashMap<String, Object> {


    private static final long serialVersionUID = 3323500050125496742L;

    public MapResult() {
        put("code", 0);
        put("msg", "success");
    }

    public static MapResult error() {
        return error(500, "未知异常，请联系管理员");
    }

    public static MapResult error(String msg) {
        return error(500, msg);
    }

    public static MapResult error(int code, String msg) {
        MapResult r = new MapResult();
        r.put("code", code);
        r.put("msg", msg);
        return r;
    }

    public static MapResult ok(String msg) {
        MapResult r = new MapResult();
        r.put("msg", msg);
        return r;
    }

    public static MapResult ok(Map<String, Object> map) {
        MapResult r = new MapResult();
        r.putAll(map);
        return r;
    }

    public static MapResult ok() {
        return new MapResult();
    }

    @Override
    public MapResult put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}
