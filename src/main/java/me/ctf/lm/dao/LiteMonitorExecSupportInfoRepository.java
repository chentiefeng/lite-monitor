package me.ctf.lm.dao;

import me.ctf.lm.entity.LiteMonitorExecSupportInfoEntity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author: chentiefeng[chentiefeng@linzikg.com]
 * @create: 2019-12-12 13:56
 */
@ConditionalOnExpression(value = "'${monitor.distributed-lock-type:db}'.equalsIgnoreCase('db') || '${monitor.distribute-task-type:http}'.equalsIgnoreCase('http')")
@Repository
public interface LiteMonitorExecSupportInfoRepository extends JpaRepository<LiteMonitorExecSupportInfoEntity, Long> {
    /**
     * 删除
     *
     * @param infoType
     * @param info
     */
    @Modifying
    @Query(value = "delete from lite_monitor_exec_support_info where info_type = ?1 and info = ?2", nativeQuery = true)
    void deleteByLockName(String infoType, String info);


    /**
     * 插入
     *
     * @param infoType
     * @param info
     */
    @Modifying
    @Query(value = "insert into lite_monitor_exec_support_info(info_type,info,gmt_create,gmt_modified) values(?1,?2,now(),now())", nativeQuery = true)
    void insertByLockName(String infoType, String info);

    /**
     * where info_type = ?1 and info = ?2
     *
     * @param infoType
     * @param info
     * @return
     */
    LiteMonitorExecSupportInfoEntity findByInfoTypeAndInfo(String infoType, String info);

    /**
     * where info_type = ?1 and gmt_modified >= ?2
     *
     * @param infoType
     * @param gmtCreate
     * @return
     */
    List<LiteMonitorExecSupportInfoEntity> findByInfoTypeAndGmtModifiedGreaterThanEqual(String infoType, Date gmtCreate);
}
