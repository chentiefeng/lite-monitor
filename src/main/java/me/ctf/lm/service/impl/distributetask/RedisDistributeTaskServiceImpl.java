package me.ctf.lm.service.impl.distributetask;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import me.ctf.lm.enums.HostStateEnum;
import me.ctf.lm.schedule.ScheduleCmdExecutor;
import me.ctf.lm.service.DistributeTaskService;
import me.ctf.lm.util.MonitorConfigUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author: chentiefeng[chentiefeng@linzikg.com]
 * @create: 2019-12-20 14:59
 */
@ConditionalOnExpression(value = "'${monitor.distribute-task-type:http}'.equalsIgnoreCase('redis')")
@Component
@Slf4j
public class RedisDistributeTaskServiceImpl implements DistributeTaskService<Long[]>, CommandLineRunner {
    private final static String MONITOR_TASK = "monitorTaskQueue";
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(), new ThreadFactoryBuilder().setNameFormat("thread-task-consumer-%s").build());

    @Override
    public void distribute(Long[] longs) {
        String collect = Arrays.stream(longs).map(String::valueOf).collect(Collectors.joining(","));
        stringRedisTemplate.opsForList().leftPush(MONITOR_TASK, collect);
    }


    @Override
    public void run(String... args) {
        threadPoolExecutor.execute(() -> {
            if (MonitorConfigUtil.getCluster()) {
                log.info("redis distribute task service start consumption.");
                do {
                    String value = stringRedisTemplate.opsForList().rightPop(MONITOR_TASK, 1, TimeUnit.SECONDS);
                    if (StringUtils.isBlank(value)) {
                        continue;
                    }
                    Long[] ids = Arrays.stream(value.split(",")).map(Long::valueOf).toArray(Long[]::new);
                    ScheduleCmdExecutor.execute(ids);
                } while (HostStateEnum.ONLINE.name().equalsIgnoreCase(MonitorConfigUtil.getHostState()));
            }
        });
    }
}
