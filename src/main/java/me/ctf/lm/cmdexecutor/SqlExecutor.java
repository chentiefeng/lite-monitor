package me.ctf.lm.cmdexecutor;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import lombok.extern.slf4j.Slf4j;
import me.ctf.lm.entity.MonitorConfigEntity;
import me.ctf.lm.enums.DingTypeEnum;
import me.ctf.lm.enums.MonitorTypeEnum;
import me.ctf.lm.util.DingMarkdownMessage;
import me.ctf.lm.util.DingTalkHelper;
import me.ctf.lm.util.FeishuTalkHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static me.ctf.lm.cmdexecutor.LogCmdExecutor.ALL;

/**
 * @author chentiefeng
 * @date 2020-09-11 16:18
 */
@Component
@Slf4j
public class SqlExecutor extends AbstractCmdExecutor {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Override
    public void execute(MonitorConfigEntity monitor) {
        try {
            DynamicDataSourceContextHolder.push(monitor.getSchemaName());
            List<Map<String, Object>> maps = jdbcTemplate.queryForList(monitor.getScript());
            List<Object> values = new ArrayList<>();
            for (Map<String, Object> map : maps) {
                values.addAll(map.values());
            }
            List<String> list = values.stream().filter(Objects::nonNull).map(Object::toString).collect(Collectors.toList());
            if (list.size() < monitor.getThreshold()) {
                return;
            }
            //发送订单消息
            if (DingTypeEnum.FEISHU.name().equalsIgnoreCase(monitor.getDingType())) {
                feishu(monitor, list);
            } else {
                ding(monitor, list);
            }
        } finally {
            DynamicDataSourceContextHolder.poll();
        }
    }

    /**
     * 钉钉消息发送
     *
     * @param monitor
     * @param list
     */
    private void ding(MonitorConfigEntity monitor, List<String> list) {
        DingMarkdownMessage message = new DingMarkdownMessage();
        String title = monitor.getSchemaName() + "," + monitor.getDingTitle() + "[" + list.size() + "]";
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
        for (String s : list) {
            message.add(DingMarkdownMessage.getReferenceText(s + "\n"));
        }
        if(StringUtils.isNotBlank(monitor.getSignKey())) {
            long timestamp = System.currentTimeMillis();
            try {
                String sign = DingTalkHelper.sign(timestamp, monitor.getSignKey());
                DingTalkHelper.sendMarkdownMsg(message, monitor.getDingToken(), timestamp, sign);
            } catch (NoSuchAlgorithmException | UnsupportedEncodingException | InvalidKeyException e) {
                log.error(e.getMessage(), e);
            }
        }else {
            DingTalkHelper.sendMarkdownMsg(message, monitor.getDingToken());
        }
    }

    /**
     * 飞书消息发送
     *
     * @param monitor
     * @param list
     */
    private void feishu(MonitorConfigEntity monitor, List<String> list) {
        String title = monitor.getSchemaName() + "," + monitor.getDingTitle() + "[" + list.size() + "]";
        if (monitor.getShowCount() == null) {
            monitor.setShowCount(10);
        }
        if (list.size() > monitor.getShowCount()) {
            title = title + "[展示前" + monitor.getShowCount() + "个]";
            list = list.subList(0, monitor.getShowCount());
        }
        String dingAt = monitor.getDingAt();
        boolean atAll = ALL.equalsIgnoreCase(dingAt);
        if (!atAll && StringUtils.isNoneBlank(dingAt)) {
            String[] atMobiles = dingAt.split(",");
            title = title + ",@" + String.join(",@", atMobiles);
        }
        if (atAll) {
            title = title + ",@all";
        }
        StringBuilder content = new StringBuilder();
        for (String s : list) {
            content.append(s).append("\n");
        }
        FeishuTalkHelper.sendTextMsg(title, content.toString(), monitor.getDingToken());
    }

    @Override
    protected MonitorTypeEnum monitorType() {
        return MonitorTypeEnum.SQL;
    }
}
