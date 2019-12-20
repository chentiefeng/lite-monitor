package me.ctf.lm.cmdexecutor;

import lombok.extern.slf4j.Slf4j;
import me.ctf.lm.entity.LiteMonitorConfigEntity;
import me.ctf.lm.enums.MonitorTypeEnum;
import me.ctf.lm.util.DingMarkdownMessage;
import me.ctf.lm.util.DingTalkHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

/**
 * @author: chentiefeng[chentiefeng@linzikg.com]
 * @create: 2019-12-18 11:35
 */
@Slf4j
@Component
public class PsCmdExecutor extends AbstractCmdExecutor {
    private static final String ALL = "all";

    /**
     * 日志命令执行
     *
     * @param monitor
     */
    @Override
    public void execute(LiteMonitorConfigEntity monitor) {
        Date now = new Date();
        Date pre = DateUtils.addSeconds(now, -monitor.getStatSecond().intValue());
        String nowStr = DateFormatUtils.format(now, "yyyy-MM-dd HH:mm:ss");
        String preStr = DateFormatUtils.format(pre, "yyyy-MM-dd HH:mm:ss");
        //命令构建
        String cmd = "ps -ef|grep '" + monitor.getShellCmd() + "'|grep -v grep|awk '{print $2}'";
        //执行命令
        String psId = null;
        try {
            psId = cmdExecute(monitor, cmd);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        //进程号为空发送钉钉消息
        if (StringUtils.isBlank(psId)) {
            //发送订单消息
            ding(monitor, nowStr, preStr);
        }
    }


    /**
     * 钉钉消息发送
     *
     * @param monitor
     * @param nowStr
     * @param preStr
     */
    private void ding(LiteMonitorConfigEntity monitor, String nowStr, String preStr) {
        DingMarkdownMessage message = new DingMarkdownMessage();
        String title = monitor.getHostName() + "," + monitor.getRemark();
        String dingAt = monitor.getDingAt();
        boolean atAll = ALL.equalsIgnoreCase(dingAt);
        message.setAtAll(atAll);
        if (!atAll && StringUtils.isNoneBlank(dingAt)) {
            String[] atMobiles = dingAt.split(",");
            message.setAtMobiles(atMobiles);
            title = title + String.join(",@", atMobiles);
        }
        if (atAll) {
            title = title + ",@all";
        }
        message.setTitle(title);
        message.add(DingMarkdownMessage.getHeaderText(3, title));
        message.add(DingMarkdownMessage.getReferenceText("统计时段:" + preStr + "--" + nowStr + "\n"));
        message.add(DingMarkdownMessage.getReferenceText("进程不存在，请及时处理"));
        DingTalkHelper.sendMarkdownMsg(message, monitor.getDingToken());
    }

    @Override
    protected MonitorTypeEnum monitorType() {
        return MonitorTypeEnum.PROCESS;
    }
}
