package me.ctf.lm.schedule;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import me.ctf.lm.cmdexecutor.AbstractCmdExecutor;
import me.ctf.lm.entity.LiteMonitorConfigEntity;
import me.ctf.lm.enums.MonitorEnableEnum;
import me.ctf.lm.enums.MonitorTypeEnum;
import me.ctf.lm.service.DistributeTaskService;
import me.ctf.lm.service.DistributedLockService;
import me.ctf.lm.service.LiteMonitorConfigService;
import me.ctf.lm.util.MonitorConfigUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: chentiefeng[chentiefeng@linzikg.com]
 * @create: 2019-12-18 13:45
 */
@Slf4j
@Component
public class ScheduleCmdExecutor {
    /**
     * 命令执行器Map
     */
    private static Map<MonitorTypeEnum, AbstractCmdExecutor> cmdExecutorMap = new HashMap<>();
    private static LiteMonitorConfigService liteMonitorConfigService;
    private static DistributedLockService distributedLockService;
    private static DistributeTaskService<Long[]> distributeTaskService;
    private static ThreadPoolExecutor executor;

    static {
        executor = new ThreadPoolExecutor(5, 20, 1000, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(100),
                new ThreadFactoryBuilder().setNameFormat("cmd-executor-%s").build());
    }

    /**
     * 注册命令执行器
     *
     * @param monitorType
     * @param cmdExecutor
     */
    public static void register(MonitorTypeEnum monitorType, AbstractCmdExecutor cmdExecutor) {
        cmdExecutorMap.put(monitorType, cmdExecutor);
        log.info("{} register cmd executor success.", monitorType);
    }

    /**
     * 执行monitor
     *
     * @param ids
     */
    public static void execute(Long[] ids) {
        if (Objects.isNull(ids) || ids.length == 0) {
            return;
        }
        List<LiteMonitorConfigEntity> monitors = liteMonitorConfigService.findByIds(ids);
        execute(monitors);
    }

    /**
     * 执行
     *
     * @param monitors
     */
    private static void execute(List<LiteMonitorConfigEntity> monitors) {
        for (LiteMonitorConfigEntity monitor : monitors) {
            int threadNum = executor.getPoolSize() + executor.getQueue().size();
            //120 = queue.size + maximumPoolSize
            int waitTime = 0;
            while (threadNum >= 120) {
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e) {
                    log.error(e.getMessage(), e);
                }
                threadNum = executor.getPoolSize() + executor.getQueue().size();
                waitTime++;
                //等待超过5秒，直接抛弃，也可以重新分发到其他机器
                if (waitTime > 10) {
                    return;
                }
            }
            executor.execute(() -> {
                try {
                    log.info("monitor {} execute task start.", monitor.getRemark());
                    cmdExecutorMap.get(MonitorTypeEnum.valueOf(monitor.getMonitorType())).execute(monitor);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                } finally {
                    log.info("monitor {} execute task finish.", monitor.getRemark());
                }
            });
        }
    }

    /**
     * 按照频率找到所有监控任务分发
     *
     * @param frequency
     */
    public static void distribute(String frequency) {
        log.info("frequency {} schedule executor start.", frequency);
        if (MonitorConfigUtil.getCluster()) {
            cluster(frequency);
        } else {
            single(frequency);
        }
        log.info("frequency {} schedule executor finish.", frequency);
    }

    /**
     * 单机
     *
     * @param frequency
     */
    private static void single(String frequency) {
        Pageable pageable = PageRequest.of(0, 100);
        Page<LiteMonitorConfigEntity> page;
        do {
            page = liteMonitorConfigService.page(null, null, frequency, MonitorEnableEnum.ENABLED.getVal(), pageable);
            if (page.isEmpty()) {
                return;
            }
            execute(page.getContent());
        } while (page.hasNext());
    }

    /**
     * 集群
     *
     * @param frequency
     */
    private static void cluster(String frequency) {
        try {
            boolean lock = distributedLockService.lock(frequency);
            if (!lock) {
                log.warn("frequency {} fetch lock failed.", frequency);
                return;
            }
            Long[] ids = new Long[10];
            Arrays.fill(ids, -1L);
            Pageable pageable = PageRequest.of(0, 100);
            Page<LiteMonitorConfigEntity> page;
            do {
                page = liteMonitorConfigService.page(null, null, frequency, MonitorEnableEnum.ENABLED.getVal(), pageable);
                if (page.isEmpty()) {
                    return;
                }
                // 每10个分发
                int idx = 0;
                for (LiteMonitorConfigEntity monitor : page) {
                    ids[idx] = monitor.getId();
                    if (idx == 9) {
                        idx = 0;
                        send(ids);
                        Arrays.fill(ids, -1L);
                    }
                    idx++;
                }
                if (ids[0] != -1L) {
                    send(ids);
                }
            } while (page.hasNext());
        } finally {
            distributedLockService.release(frequency);
        }
    }

    /**
     * 发送
     *
     * @param ids
     */
    private static void send(Long[] ids) {
        try {
            distributeTaskService.distribute(Arrays.stream(ids).filter(id -> id > -1L).toArray(Long[]::new));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Resource
    public void setLiteMonitorService(LiteMonitorConfigService liteMonitorConfigService) {
        ScheduleCmdExecutor.liteMonitorConfigService = liteMonitorConfigService;
    }

    @Resource
    public void setDistributedLockService(DistributedLockService distributedLockService) {
        ScheduleCmdExecutor.distributedLockService = distributedLockService;
    }

    @Resource
    public void setDistributeTaskService(DistributeTaskService<Long[]> distributeTaskService) {
        ScheduleCmdExecutor.distributeTaskService = distributeTaskService;
    }
}
