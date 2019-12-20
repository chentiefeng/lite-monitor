package me.ctf.lm.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: chentiefeng[chentiefeng@linzikg.com]
 * @create: 2019-12-18 09:54
 */
@AllArgsConstructor
@Getter
public enum MonitorEnableEnum {
    /**
     * 禁用
     */
    DISABLED(0),
    /**
     * 启用
     */
    ENABLED(1);
    private Integer val;
}
