package me.ctf.lm.service.impl.distributetask;

import lombok.extern.slf4j.Slf4j;
import me.ctf.lm.service.DistributedLockService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.UUID;

/**
 * @author: chentiefeng[chentiefeng@linzikg.com]
 * @create: 2019-12-20 14:09
 */
@ConditionalOnExpression(value = "'${monitor.distributed-lock-type:db}'.equalsIgnoreCase('redis')")
@Service
@Slf4j
public class RedisDistributedLockServiceImpl implements DistributedLockService {

    private ThreadLocal<Deque<String>> threadLocalLockDeque = new ThreadLocal<>();
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean lock(String lockName) {
        String value = UUID.randomUUID().toString();
        try {
            Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent(lockName, value, Duration.ofSeconds(30));
            if (lock != null && lock) {
                Deque<String> lockDeque = threadLocalLockDeque.get();
                if (lockDeque == null) {
                    lockDeque = new ArrayDeque<>();
                    threadLocalLockDeque.set(lockDeque);
                }
                lockDeque.offer(value);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void release(String lockName) {
        try {
            String value = stringRedisTemplate.opsForValue().get(lockName);
            if (value == null) {
                log.warn("lock [{}] is released.", lockName);
                return;
            }
            if (value.equals(threadLocalLockDeque.get().pop())) {
                stringRedisTemplate.delete(lockName);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (threadLocalLockDeque.get() != null && threadLocalLockDeque.get().size() == 0) {
                threadLocalLockDeque.remove();
            }
        }
    }
}
