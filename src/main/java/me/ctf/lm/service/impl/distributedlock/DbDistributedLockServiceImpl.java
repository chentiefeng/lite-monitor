package me.ctf.lm.service.impl.distributedlock;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import me.ctf.lm.dao.MonitorExecSupportInfoMapper;
import me.ctf.lm.entity.MonitorExecSupportInfoEntity;
import me.ctf.lm.service.DistributedLockService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @author: chentiefeng[chentiefeng@linzikg.com]
 * @create: 2019-12-18 14:57
 */
@ConditionalOnExpression(value = "'${monitor.distributed-lock-type:db}'.equalsIgnoreCase('db')")
@Service
public class DbDistributedLockServiceImpl implements DistributedLockService {
    private static final String LOCK = "LOCK";
    @Resource
    private MonitorExecSupportInfoMapper monitorExecSupportInfoMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean lock(String lockName) {
        try {
            MonitorExecSupportInfoEntity entity = new MonitorExecSupportInfoEntity();
            entity.setInfoType(LOCK);
            entity.setInfo(lockName);
            entity.setGmtCreate(LocalDateTime.now());
            entity.setGmtModified(LocalDateTime.now());
            monitorExecSupportInfoMapper.insert(entity);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean release(String lockName) {
        monitorExecSupportInfoMapper.delete(Wrappers.<MonitorExecSupportInfoEntity>lambdaQuery()
                .eq(MonitorExecSupportInfoEntity::getInfoType, LOCK)
                .eq(MonitorExecSupportInfoEntity::getInfo, lockName));
        return true;
    }
}
