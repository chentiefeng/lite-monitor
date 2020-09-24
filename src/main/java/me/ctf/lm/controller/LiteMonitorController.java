package me.ctf.lm.controller;

import lombok.extern.slf4j.Slf4j;
import me.ctf.lm.dto.MapResult;
import me.ctf.lm.entity.MonitorConfigEntity;
import me.ctf.lm.enums.FrequencyEnum;
import me.ctf.lm.schedule.ScheduleCmdExecutor;
import me.ctf.lm.service.LiteMonitorConfigService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

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
     * @return
     */
    @GetMapping("/page")
    public MapResult page(@RequestParam Map<String, Object> params) {
        try {
            return MapResult.ok().put("page", liteMonitorConfigService.queryPage(params));
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
    public MapResult save(@RequestBody MonitorConfigEntity monitor) {
        liteMonitorConfigService.submit(monitor);
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
        liteMonitorConfigService.removeById(id);
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
     * 启用
     *
     * @param id
     * @return
     */
    @GetMapping("/info")
    public MapResult info(@RequestParam("id") Long id) {
        return MapResult.ok().put("entity", liteMonitorConfigService.info(id));
    }

    /**
     * info
     *
     * @param id
     * @return
     */
    @GetMapping("/disabled")
    public MapResult disabled(@RequestParam("id") Long id) {
        liteMonitorConfigService.disabled(id);
        return MapResult.ok();
    }

    /**
     * frequency
     *
     * @return
     */
    @GetMapping("/frequency")
    public MapResult frequency() {
        List<Map<String, String>> mapList = new ArrayList<>();
        Arrays.stream(FrequencyEnum.values()).forEach(value -> {
            Map<String, String> map = new HashMap<>(1);
            map.put("frequency", value.getCron());
            map.put("frequencyDesc", value.getDesc());
            mapList.add(map);
        });
        return MapResult.ok().put("frequencyList", mapList);
    }
}
