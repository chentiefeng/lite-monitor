package me.ctf.lm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.ctf.lm.dao.MonitorConfigMapper;
import me.ctf.lm.entity.MonitorConfigEntity;
import me.ctf.lm.enums.FrequencyEnum;
import me.ctf.lm.enums.MonitorEnableEnum;
import me.ctf.lm.enums.MonitorTypeEnum;
import me.ctf.lm.service.LiteMonitorConfigService;
import me.ctf.lm.util.PageUtils;
import me.ctf.lm.util.Query;
import me.ctf.lm.util.UpdateUtils;
import me.ctf.lm.util.validator.LogGroup;
import me.ctf.lm.util.validator.ValidatorUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author: chentiefeng[chentiefeng@linzikg.com]
 * @create: 2019-12-12 14:01
 */
@Service
public class LiteMonitorConfigServiceImpl extends ServiceImpl<MonitorConfigMapper, MonitorConfigEntity> implements LiteMonitorConfigService {
    @Resource
    private MonitorConfigMapper monitorConfigMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submit(MonitorConfigEntity entity) {
        if (MonitorTypeEnum.LOG.name().equals(entity.getMonitorType())) {
            ValidatorUtil.validateEntity(entity, LogGroup.class);
        } else {
            ValidatorUtil.validateEntity(entity);
        }
        if (Objects.nonNull(entity.getId())) {
            MonitorConfigEntity monitor = this.getById(entity.getId());
            if (monitor != null) {
                String dingAt = entity.getDingAt();
                UpdateUtils.copyNullProperties(entity, monitor);
                entity = monitor;
                entity.setDingAt(dingAt);
            }
        }
        saveOrUpdate(entity);
        return entity.getId();
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MonitorConfigEntity> page = this.page(
                new Query<MonitorConfigEntity>().getPage(params),
                new QueryWrapper<MonitorConfigEntity>().lambda()
                        .like(params.get("name") != null, MonitorConfigEntity::getHostName, params.get("name"))
                        .like(params.get("remark") != null, MonitorConfigEntity::getRemark, params.get("remark"))
                        .eq(params.get("frequency") != null, MonitorConfigEntity::getFrequency, params.get("frequency"))
                        .eq(params.get("enabled") != null, MonitorConfigEntity::getEnabled, params.get("enabled"))
        );
        for (MonitorConfigEntity monitorConfigEntity : page.getRecords()) {
            monitorConfigEntity.setFrequencyDesc(Objects.requireNonNull(FrequencyEnum.getByValue(monitorConfigEntity.getFrequency())).getDesc());
        }
        return new PageUtils(page);
    }


    @Override
    public void enabled(long id) {
        update(Wrappers.<MonitorConfigEntity>lambdaUpdate().set(MonitorConfigEntity::getEnabled, MonitorEnableEnum.ENABLED.getVal()).eq(MonitorConfigEntity::getId, id));
    }

    @Override
    public void disabled(long id) {
        update(Wrappers.<MonitorConfigEntity>lambdaUpdate().set(MonitorConfigEntity::getEnabled, MonitorEnableEnum.DISABLED.getVal()).eq(MonitorConfigEntity::getId, id));
    }

    @Override
    public MonitorConfigEntity info(Long id) {
        Optional<MonitorConfigEntity> optional = Optional.ofNullable(getById(id));
        if (optional.isPresent()) {
            optional.get().setFrequencyDesc(Objects.requireNonNull(FrequencyEnum.getByValue(optional.get().getFrequency())).getDesc());
            return optional.get();
        }
        return null;
    }
}
