package me.ctf.lm.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: chentiefeng[chentiefeng@linzikg.com]
 * @create: 2019-12-19 15:48
 */
@AllArgsConstructor
@Getter
public enum FrequencyEnum {
    //监控服务-30秒
    FREQUENCY_30("*/30 * * * * ?", "30秒"),
    //监控服务-1分钟
    FREQUENCY_60("1 */1 * * * ?", "1分钟"),
    //监控服务-5分钟
    FREQUENCY_300("1 */5 * * * ?", "5分钟"),
    //监控服务-10分钟
    FREQUENCY_600("1 */10 * * * ?", "10分钟"),
    //监控服务-30分钟
    FREQUENCY_1800("1 */30 * * * ?", "30分钟"),
    //监控服务-1小时
    FREQUENCY_3600("1 0 */1 * * ?", "1小时"),
    //监控服务-2小时
    FREQUENCY_36000("1 0 */2 * * ?", "2小时"),
    //监控服务-6小时
    FREQUENCY_21600("1 0 */6 * * ?", "6小时"),
    //监控服务-12小时
    FREQUENCY_43200("1 0 */12 * * ?", "12小时"),
    ;
    private String cron;
    private String desc;
}
