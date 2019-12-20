package me.ctf.lm.service.impl.distributetask;

import lombok.extern.slf4j.Slf4j;
import me.ctf.lm.schedule.ScheduleCmdExecutor;
import me.ctf.lm.service.DistributeTaskService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.List;

/**
 * @author: chentiefeng[chentiefeng@linzikg.com]
 * @create: 2019-12-20 15:40
 */
@ConditionalOnExpression(value = "'${monitor.distribute-task-type:http}'.equalsIgnoreCase('kafka')")
@Component
@Slf4j
public class KafkaDistributeTaskServiceImpl implements DistributeTaskService<Long[]> {
    private final static String MONITOR_TASK = "monitor-task";
    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void distribute(Long[] longs) {
        for (Long id : longs) {
            ListenableFuture<SendResult<String, String>> listenableFuture = kafkaTemplate.send(MONITOR_TASK, String.valueOf(id));
            listenableFuture.addCallback(result -> {
                        if (log.isInfoEnabled()) {
                            log.info("发送kafka消息成功,topic={},msg={}", MONITOR_TASK, id);
                        }
                    },
                    ex -> log.error(MessageFormat.format("发送kafka消息失败,topic={0},msgId={1}", MONITOR_TASK, id), ex));
        }
    }


    @KafkaListener(topics = MONITOR_TASK)
    public void execute(List<ConsumerRecord<String, String>> records, Acknowledgment ack) {
        try {
            ScheduleCmdExecutor.execute(records.stream().map(record -> Long.valueOf(record.value())).toArray(Long[]::new));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            ack.acknowledge();
        }
    }
}
