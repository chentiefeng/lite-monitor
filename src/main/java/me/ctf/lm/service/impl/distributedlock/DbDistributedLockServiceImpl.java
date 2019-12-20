package me.ctf.lm.service.impl.distributedlock;

import me.ctf.lm.dao.LiteMonitorExecSupportInfoRepository;
import me.ctf.lm.service.DistributedLockService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author: chentiefeng[chentiefeng@linzikg.com]
 * @create: 2019-12-18 14:57
 */
@ConditionalOnExpression(value = "'${monitor.distributed-lock-type:db}'.equalsIgnoreCase('db')")
@Service
public class DbDistributedLockServiceImpl implements DistributedLockService {
    private static final String LOCK = "LOCK";
    @Resource
    private LiteMonitorExecSupportInfoRepository liteMonitorExecSupportInfoRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean lock(String lockName) {
        try {
            liteMonitorExecSupportInfoRepository.insertByLockName(LOCK, lockName);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void release(String lockName) {
        liteMonitorExecSupportInfoRepository.deleteByLockName(LOCK, lockName);
    }
}
