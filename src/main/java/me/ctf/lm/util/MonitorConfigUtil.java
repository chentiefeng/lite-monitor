package me.ctf.lm.util;

import me.ctf.lm.config.MonitorConfig;
import me.ctf.lm.enums.DistributedLockTypeEnum;
import me.ctf.lm.enums.HostStateEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author: chentiefeng[chentiefeng@linzikg.com]
 * @create: 2019-12-20 11:22
 */
@Component
public class MonitorConfigUtil {
    private static MonitorConfig monitorConfig;

    public static boolean getCluster() {
        return monitorConfig.getCluster() == null ? false : monitorConfig.getCluster();
    }

    public static String getHostState() {
        if (StringUtils.isBlank(monitorConfig.getHostState())) {
            return HostStateEnum.ONLINE.name();
        }
        return monitorConfig.getHostState();
    }

    public static int getDuration() {
        return monitorConfig.getDuration() == null ? 3 : monitorConfig.getDuration();
    }

    public static String getDistributedLockType() {
        if (StringUtils.isBlank(monitorConfig.getDistributedLockType())) {
            return DistributedLockTypeEnum.DB.name();
        }
        return monitorConfig.getDistributedLockType();
    }

    @Resource
    public void setMonitorConfig(MonitorConfig monitorConfig) {
        MonitorConfigUtil.monitorConfig = monitorConfig;
    }
}
