package me.ctf.lm.controller;

import lombok.extern.slf4j.Slf4j;
import me.ctf.lm.dto.MapResult;
import me.ctf.lm.entity.LiteMonitorConfigEntity;
import me.ctf.lm.schedule.ScheduleCmdExecutor;
import me.ctf.lm.service.LiteMonitorConfigService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: chentiefeng[chentiefeng@linzikg.com]
 * @create: 2019-12-18 09:23
 */
@RestController
@RequestMapping("/liteMonitor")
@Slf4j
public class LiteMonitorController {

    @Resource
    private LiteMonitorConfigService liteMonitorConfigService;

    /**
     * 分页查询
     *
     * @param hostName
     * @param remark
     * @param page
     * @param limit
     * @return
     */
    @GetMapping("/page")
    public MapResult page(@RequestParam(value = "hostName", required = false) String hostName,
                          @RequestParam(value = "remark", required = false) String remark,
                          @RequestParam(value = "frequency", required = false) String frequency,
                          @RequestParam("page") int page, @RequestParam("limit") int limit) {
        try {
            Page<LiteMonitorConfigEntity> pageObj = liteMonitorConfigService.page(hostName, remark, frequency, null, page, limit);
            return MapResult.ok().put("page", pageObj);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return MapResult.error(e.getMessage());
        }
    }

    /**
     * 执行
     *
     * @param ids
     * @return
     */
    @PostMapping("/execute")
    public MapResult execute(@RequestBody Long[] ids) {
        ScheduleCmdExecutor.execute(ids);
        return MapResult.ok();
    }

    /**
     * 保存
     *
     * @param monitor
     * @return
     */
    @PostMapping("/save")
    public MapResult save(@RequestBody LiteMonitorConfigEntity monitor) {
        liteMonitorConfigService.save(monitor);
        return MapResult.ok();
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @GetMapping("/delete")
    public MapResult delete(@RequestParam("id") Long id) {
        liteMonitorConfigService.delete(id);
        return MapResult.ok();
    }

    /**
     * 启用
     *
     * @param id
     * @return
     */
    @GetMapping("/enabled")
    public MapResult enabled(@RequestParam("id") Long id) {
        liteMonitorConfigService.enabled(id);
        return MapResult.ok();
    }

    /**
     * 禁用
     *
     * @param id
     * @return
     */
    @GetMapping("/disabled")
    public MapResult disabled(@RequestParam("id") Long id) {
        liteMonitorConfigService.disabled(id);
        return MapResult.ok();
    }
}
