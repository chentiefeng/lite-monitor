package me.ctf.lm.cmdexecutor;

import lombok.extern.slf4j.Slf4j;
import me.ctf.lm.entity.LiteMonitorConfigEntity;
import me.ctf.lm.enums.MonitorTypeEnum;
import me.ctf.lm.util.DateTimeRegex;
import me.ctf.lm.util.DingMarkdownMessage;
import me.ctf.lm.util.DingTalkHelper;
import me.ctf.lm.util.PlaceholderUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: chentiefeng[chentiefeng@linzikg.com]
 * @create: 2019-12-18 10:52
 */
@Slf4j
@Component
public class LogCmdExecutor extends AbstractCmdExecutor {

    public static final String ALL = "all";

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
        String cmd = cmdBuilder(monitor, pre);
        //执行命令
        List<String> list = executeAndParse(monitor, cmd);
        if (list == null) {
            return;
        }
        if (list.size() < monitor.getThreshold()) {
            return;
        }
        //发送订单消息
        ding(monitor, nowStr, preStr, list);
    }

    /**
     * 命令执行
     *
     * @param monitor
     * @param cmd
     * @return
     */
    protected List<String> executeAndParse(LiteMonitorConfigEntity monitor, String cmd) {
        String rst = null;
        try {
            rst = super.cmdExecute(monitor, cmd);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        if (StringUtils.isBlank(rst)) {
            return null;
        }
        return Arrays.stream(StringUtils.split(rst, "\n")).collect(Collectors.toList());
    }


    /**
     * 构建命令
     *
     * @param monitor
     * @param pre
     * @return
     */
    private String cmdBuilder(LiteMonitorConfigEntity monitor, Date pre) {
        StringBuilder cmd = new StringBuilder("grep");
        for (String regex : DateTimeRegex.dateUntilNowFormat(pre)) {
            cmd.append(" -e '").append(regex).append("'");
        }
        //2019.12.26 日志文件路径支持日期占位符
        cmd.append(" ").append(PlaceholderUtil.analyze(monitor.getFilePath(), null));
        if (StringUtils.isNoneBlank(monitor.getShellCmd())) {
            cmd.append(" | ").append(monitor.getShellCmd());
        }
        return cmd.toString();
    }

    /**
     * 钉钉消息发送
     *
     * @param monitor
     * @param nowStr
     * @param preStr
     * @param list
     */
    private void ding(LiteMonitorConfigEntity monitor, String nowStr, String preStr, List<String> list) {
        DingMarkdownMessage message = new DingMarkdownMessage();
        String title = monitor.getHostName() + "," + monitor.getDingTitle() + "[" + list.size() + "]";
        if (monitor.getShowCount() == null) {
            monitor.setShowCount(10);
        }
        if (list.size() > monitor.getShowCount()) {
            title = title + "[展示前" + monitor.getShowCount() + "个]";
            list = list.subList(0, monitor.getShowCount());
        }
        String dingAt = monitor.getDingAt();
        boolean atAll = ALL.equalsIgnoreCase(dingAt);
        message.setAtAll(atAll);
        if (!atAll && StringUtils.isNoneBlank(dingAt)) {
            String[] atMobiles = dingAt.split(",");
            message.setAtMobiles(atMobiles);
            title = title + ",@" + String.join(",@", atMobiles);
        }
        if (atAll) {
            title = title + ",@all";
        }
        message.setTitle(title);
        message.add(DingMarkdownMessage.getHeaderText(3, title));
        message.add(DingMarkdownMessage.getReferenceText("统计时段:" + preStr + "--" + nowStr + "\n"));
        for (String s : list) {
            message.add(DingMarkdownMessage.getReferenceText(s + "\n"));
        }
        DingTalkHelper.sendMarkdownMsg(message, monitor.getDingToken());
    }

    @Override
    protected MonitorTypeEnum monitorType() {
        return MonitorTypeEnum.LOG;
    }
}
