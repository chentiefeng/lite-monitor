package me.ctf.lm.service;

import me.ctf.lm.entity.LiteMonitorConfigEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author: chentiefeng[chentiefeng@linzikg.com]
 * @create: 2019-12-12 14:00
 */
public interface LiteMonitorConfigService {
    /**
     * 保存
     *
     * @param entity
     * @return
     */
    Long save(LiteMonitorConfigEntity entity);

    /**
     * 分页查询
     *
     * @param hostName
     * @param remark
     * @param frequency
     * @param enabled
     * @param page      从1开始
     * @param limit
     * @return
     */
    Page<LiteMonitorConfigEntity> page(String hostName, String remark, String frequency, Integer enabled, int page, int limit);

    /**
     * 分页查询
     *
     * @param hostName
     * @param remark
     * @param frequency
     * @param enabled
     * @param pageable  从0开始
     * @return
     */
    Page<LiteMonitorConfigEntity> page(String hostName, String remark, String frequency, Integer enabled, Pageable pageable);

    /**
     * 删除
     *
     * @param id
     */
    void delete(long id);

    /**
     * 启用
     *
     * @param id
     */
    void enabled(long id);

    /**
     * 禁用
     *
     * @param id
     */
    void disabled(long id);

    /**
     * 根据id数组获取
     *
     * @param ids
     */
    List<LiteMonitorConfigEntity> findByIds(Long[] ids);

    /**
     * info
     * @param id
     * @return
     */
    LiteMonitorConfigEntity info(Long id);
}
