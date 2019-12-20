package me.ctf.lm.cmdexecutor;

import me.ctf.lm.entity.LiteMonitorConfigEntity;
import me.ctf.lm.enums.MonitorTypeEnum;
import me.ctf.lm.schedule.ScheduleCmdExecutor;
import me.ctf.lm.util.CmdExecutorUtil;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * @author: chentiefeng[chentiefeng@linzikg.com]
 * @create: 2019-12-18 13:41
 */
public abstract class AbstractCmdExecutor {
    /**
     * 命令执行
     *
     * @param monitor
     */
    public abstract void execute(LiteMonitorConfigEntity monitor);

    @PostConstruct
    public void register() {
        ScheduleCmdExecutor.register(monitorType(), this);
    }

    /**
     * 监控类型
     *
     * @return
     */
    protected abstract MonitorTypeEnum monitorType();

    /**
     * 命令执行
     *
     * @param monitor
     * @param cmd
     * @return
     */
    protected String cmdExecute(LiteMonitorConfigEntity monitor, String cmd) throws IOException {
        if (StringUtils.isNoneBlank(monitor.getPwd())) {
            // 用户名密码认证
            return CmdExecutorUtil.authPasswordAndExecute(monitor.getHostName(), monitor.getPort(), monitor.getUsername(), monitor.getPwd(), cmd);
        } else if (StringUtils.isNoneBlank(monitor.getPem())) {
            // 用户密钥认证
            return CmdExecutorUtil.authPublicKeyAndExecute(monitor.getHostName(), monitor.getPort(), monitor.getUsername(), monitor.getPem(), cmd);
        } else {
            // 默认密钥认证
            return CmdExecutorUtil.authPublicKeyAndExecute(monitor.getHostName(), monitor.getPort(), monitor.getUsername(), cmd);
        }
    }
}
