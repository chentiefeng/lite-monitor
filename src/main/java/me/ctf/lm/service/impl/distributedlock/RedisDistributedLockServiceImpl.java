package me.ctf.lm.service.impl.distributedlock;

import lombok.extern.slf4j.Slf4j;
import me.ctf.lm.service.DistributedLockService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.*;

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

    private static DefaultRedisScript<Long> defaultRedisScript;
    private static final Long RELEASE_SUCCESS = 1L;

    static {
        defaultRedisScript = new DefaultRedisScript<>();
        defaultRedisScript.setResultType(Long.class);
        defaultRedisScript.setScriptText("if redis.call('get', KEYS[1]) == KEYS[2] then return redis.call('del', KEYS[1]) else return 0 end");
    }

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
    public boolean release(String lockName) {
        try {
            List<String> args = new ArrayList<>();
            args.add(lockName);
            args.add(threadLocalLockDeque.get().pop());
            Long result = stringRedisTemplate.execute(defaultRedisScript, args);
            return RELEASE_SUCCESS.equals(result);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (threadLocalLockDeque.get() != null && threadLocalLockDeque.get().size() == 0) {
                threadLocalLockDeque.remove();
            }
        }
        return false;
    }
}