package me.ctf.lm.entity;

import lombok.Data;
import me.ctf.lm.util.validator.LogGroup;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 轻量监控配置
 *
 * @author: chentiefeng[chentiefeng@linzikg.com]
 * @create: 2019-12-12 11:53
 */
@Entity
@Data
@Table(name = "lite_monitor_config")
public class LiteMonitorConfigEntity {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 监控类型，进程：PROCESS，日志：LOG
     */
    @Column(nullable = false)
    @NotBlank(message = "监控类型不能为空")
    private String monitorType;
    /**
     * 频率，cron表达式或者枚举
     */
    @Column(nullable = false)
    @NotBlank(message = "监控频率不能为空")
    private String frequency;

    @Transient
    private String frequencyDesc;
    /**
     * 主机
     */
    @Column(nullable = false)
    @NotBlank(message = "主机不能为空")
    private String hostName;
    /**
     * 用户名
     */
    @Column(nullable = false)
    @NotBlank(message = "用户名不能为空")
    private String username;
    /**
     * 密码
     */
    private String pwd;
    /**
     * 密钥
     */
    private String pem;
    /**
     * 端口
     */
    @NotNull(message = "端口不能为空")
    @Column(nullable = false)
    private Integer port;
    /**
     * 文件地址
     */
    @NotBlank(message = "日志文件不能为空", groups = LogGroup.class)
    private String filePath;
    /**
     * 统计范围，秒
     */
    @NotNull(message = "统计范围不能为空", groups = LogGroup.class)
    private Long statSecond;
    /**
     * 阈值
     */
    @NotNull(message = "阈值不能为空", groups = LogGroup.class)
    private Long threshold;
    /**
     * 命令
     */
    @NotBlank(message = "命令不能为空")
    private String shellCmd;
    /**
     * 钉钉标题
     */
    @NotBlank(message = "钉钉标题不能为空", groups = LogGroup.class)
    private String dingTitle;
    /**
     * 钉钉token
     */
    @NotBlank(message = "钉钉token不能为空")
    private String dingToken;
    /**
     * 钉钉展示条数
     */
    @NotNull(message = "钉钉展示条数不能为空", groups = LogGroup.class)
    private Integer showCount;
    /**
     * 钉钉at
     */
    private String dingAt;
    /**
     * 备注
     */
    private String remark;
    /**
     * 是否启用，0未启用，1启用
     */
    private Integer enabled;
    /**
     * 创建日期
     */
    private Date gmtCreate;
    /**
     * 修改日期
     */
    private Date gmtModified;
}
