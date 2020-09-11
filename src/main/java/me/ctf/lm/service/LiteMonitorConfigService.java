package me.ctf.lm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.ctf.lm.entity.MonitorConfigEntity;
import me.ctf.lm.util.PageUtils;

import java.util.Map;

/**
 * @author: chentiefeng[chentiefeng@linzikg.com]
 * @create: 2019-12-12 14:00
 */
public interface LiteMonitorConfigService extends IService<MonitorConfigEntity> {
    /**
     * 保存
     *
     * @param entity
     * @return
     */
    Long submit(MonitorConfigEntity entity);

    PageUtils queryPage(Map<String, Object> params);


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
     * info
     *
     * @param id
     * @return
     */
    MonitorConfigEntity info(Long id);
}
