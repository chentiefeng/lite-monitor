package me.ctf.lm.dao;

import me.ctf.lm.entity.LiteMonitorConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author: chentiefeng[chentiefeng@linzikg.com]
 * @create: 2019-12-12 13:56
 */
@Repository
public interface LiteMonitorConfigRepository extends JpaRepository<LiteMonitorConfigEntity, Long>, JpaSpecificationExecutor<LiteMonitorConfigEntity> {
}
