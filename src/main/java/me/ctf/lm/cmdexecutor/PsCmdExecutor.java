package me.ctf.lm.cmdexecutor;

import lombok.extern.slf4j.Slf4j;
import me.ctf.lm.entity.MonitorConfigEntity;
import me.ctf.lm.enums.DingTypeEnum;
import me.ctf.lm.enums.MonitorTypeEnum;
import me.ctf.lm.util.DingMarkdownMessage;
import me.ctf.lm.util.DingTalkHelper;
import me.ctf.lm.util.FeishuTalkHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;

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
    public void execute(MonitorConfigEntity monitor) {
        //命令构建
        String cmd = "ps -ef|grep '" + monitor.getScript() + "'|grep -v grep|awk '{print $2}'";
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
            if (DingTypeEnum.FEISHU.name().equalsIgnoreCase(monitor.getDingType())) {
                feishu(monitor);
            } else {
                ding(monitor);
            }
        }
    }


    /**
     * 钉钉消息发送
     *
     * @param monitor
     */
    private void ding(MonitorConfigEntity monitor) {
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
        message.add(DingMarkdownMessage.getReferenceText("进程不存在，请及时处理"));
        DingTalkHelper.sendMarkdownMsg(message, monitor.getDingToken());
    }

    /**
     * 钉钉消息发送
     *
     * @param monitor
     */
    private void feishu(MonitorConfigEntity monitor) {
        String title = monitor.getHostName() + "," + monitor.getRemark();
        String dingAt = monitor.getDingAt();
        boolean atAll = ALL.equalsIgnoreCase(dingAt);
        if (!atAll && StringUtils.isNoneBlank(dingAt)) {
            String[] atMobiles = dingAt.split(",");
            title = title + String.join(",@", atMobiles);
        }
        if (atAll) {
            title = title + ",@all";
        }
        FeishuTalkHelper.sendTextMsg(title, "进程不存在，请及时处理", monitor.getDingToken());
    }

    @Override
    protected MonitorTypeEnum monitorType() {
        return MonitorTypeEnum.PROCESS;
    }
}
