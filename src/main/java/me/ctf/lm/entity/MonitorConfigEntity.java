package me.ctf.lm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import me.ctf.lm.util.validator.LogGroup;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 轻量监控配置
 *
 * @author: chentiefeng[chentiefeng@linzikg.com]
 * @create: 2019-12-12 11:53
 */
@Data
@TableName("lite_monitor_config")
public class MonitorConfigEntity {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 监控类型，进程：PROCESS，日志：LOG
     */
    @NotBlank(message = "监控类型不能为空")
    private String monitorType;
    /**
     * 频率，cron表达式或者枚举
     */
    @NotBlank(message = "监控频率不能为空")
    private String frequency;

    @TableField(exist = false)
    private String frequencyDesc;
    /**
     * schema
     */
    private String schemaName;
    /**
     * 主机
     */
    @NotBlank(message = "主机不能为空")
    private String hostName;
    /**
     * 用户名
     */
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
     * 脚本
     */
    @NotBlank(message = "脚本不能为空")
    private String script;
    /**
     * 提醒类型
     */
    private String dingType;
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
    private LocalDateTime gmtCreate;
    /**
     * 修改日期
     */
    private LocalDateTime gmtModified;
}
