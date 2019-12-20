package me.ctf.lm.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author: chentiefeng[chentiefeng@linzikg.com]
 * @create: 2019-12-20 11:17
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "monitor")
public class MonitorConfig {
    /**
     * 主机状态
     */
    private String hostState;
    /**
     * 是否集群
     */
    private Boolean cluster;
    /**
     * 主机检查时间，分钟
     */
    private Integer duration;
    /**
     * 分发任务模式
     */
    private String distributedLockType;
}
