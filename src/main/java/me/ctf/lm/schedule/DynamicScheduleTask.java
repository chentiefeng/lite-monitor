package me.ctf.lm.schedule;

import lombok.extern.slf4j.Slf4j;
import me.ctf.lm.enums.FrequencyEnum;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

/**
 * @author: chentiefeng[chentiefeng@linzikg.com]
 * @create: 2019-12-18 14:14
 */
@Slf4j
@Configuration
public class DynamicScheduleTask implements SchedulingConfigurer {

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        for (FrequencyEnum frequency : FrequencyEnum.values()) {
            log.info("add trigger task by frequency {}", frequency);
            taskRegistrar.addTriggerTask(
                    //添加任务内容(Runnable)
                    () -> ScheduleCmdExecutor.distribute(frequency.getCron()),
                    //设置执行周期(Trigger)
                    new CronTrigger(frequency.getCron())
            );
        }
    }
}
