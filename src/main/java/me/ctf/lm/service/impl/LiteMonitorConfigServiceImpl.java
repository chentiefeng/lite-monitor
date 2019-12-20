package me.ctf.lm.service.impl;

import me.ctf.lm.dao.LiteMonitorConfigRepository;
import me.ctf.lm.entity.LiteMonitorConfigEntity;
import me.ctf.lm.enums.MonitorEnableEnum;
import me.ctf.lm.enums.MonitorTypeEnum;
import me.ctf.lm.service.LiteMonitorConfigService;
import me.ctf.lm.util.UpdateUtils;
import me.ctf.lm.util.validator.LogGroup;
import me.ctf.lm.util.validator.ValidatorUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.util.*;

/**
 * @author: chentiefeng[chentiefeng@linzikg.com]
 * @create: 2019-12-12 14:01
 */
@Service
public class LiteMonitorConfigServiceImpl implements LiteMonitorConfigService {
    @Resource
    private LiteMonitorConfigRepository liteMonitorConfigRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long save(LiteMonitorConfigEntity entity) {
        if (MonitorTypeEnum.LOG.name().equals(entity.getMonitorType())) {
            ValidatorUtil.validateEntity(entity, LogGroup.class);
        } else {
            ValidatorUtil.validateEntity(entity);
        }
        if (Objects.nonNull(entity.getId())) {
            Optional<LiteMonitorConfigEntity> optional = liteMonitorConfigRepository.findById(entity.getId());
            if (optional.isPresent()) {
                LiteMonitorConfigEntity monitor = optional.get();
                String dingAt = entity.getDingAt();
                UpdateUtils.copyNullProperties(entity, monitor);
                entity = monitor;
                entity.setDingAt(dingAt);
            }
        }
        return liteMonitorConfigRepository.save(entity).getId();
    }

    @Override
    public Page<LiteMonitorConfigEntity> page(String hostName, String remark, String frequency, Integer enabled, int page, int limit) {
        page--;
        Pageable pageable = PageRequest.of(page, limit);
        return page(hostName, remark, frequency, enabled, pageable);
    }

    @Override
    public Page<LiteMonitorConfigEntity> page(String hostName, String remark, String frequency, Integer enabled, Pageable pageable) {
        return this.liteMonitorConfigRepository.findAll((Specification<LiteMonitorConfigEntity>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> list = new ArrayList<>();
            if (!StringUtils.isEmpty(hostName)) {
                list.add(criteriaBuilder.equal(root.get("hostName"), hostName));
            }
            if (!StringUtils.isEmpty(remark)) {
                list.add(criteriaBuilder.like(root.get("remark"), remark));
            }
            if (!StringUtils.isEmpty(frequency)) {
                list.add(criteriaBuilder.equal(root.get("frequency"), frequency));
            }
            if (Objects.nonNull(enabled)) {
                list.add(criteriaBuilder.equal(root.get("enabled"), enabled));
            }
            Predicate[] predicates = new Predicate[list.size()];
            return criteriaBuilder.and(list.toArray(predicates));
        }, pageable);
    }


    @Override
    public void delete(long id) {
        liteMonitorConfigRepository.deleteById(id);
    }

    @Override
    public void enabled(long id) {
        Optional<LiteMonitorConfigEntity> optional = liteMonitorConfigRepository.findById(id);
        optional.ifPresent(entity -> {
            entity.setEnabled(MonitorEnableEnum.ENABLED.getVal());
            liteMonitorConfigRepository.save(entity);
        });
    }

    @Override
    public void disabled(long id) {
        Optional<LiteMonitorConfigEntity> optional = liteMonitorConfigRepository.findById(id);
        optional.ifPresent(entity -> {
            entity.setEnabled(MonitorEnableEnum.DISABLED.getVal());
            liteMonitorConfigRepository.save(entity);
        });
    }

    @Override
    public List<LiteMonitorConfigEntity> findByIds(Long[] ids) {

        return liteMonitorConfigRepository.findAll((Specification<LiteMonitorConfigEntity>) (root, criteriaQuery, criteriaBuilder) -> {
            CriteriaBuilder.In<Object> in = criteriaBuilder.in(root.get("id"));
            Arrays.stream(ids).forEach(in::value);
            return in;
        });
    }
}
