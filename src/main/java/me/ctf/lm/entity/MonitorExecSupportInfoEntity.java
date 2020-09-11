package me.ctf.lm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author: chentiefeng[chentiefeng@linzikg.com]
 * @create: 2019-12-18 14:54
 */
@Data
@TableName("lite_monitor_exec_support_info")
public class MonitorExecSupportInfoEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * info type
     */
    private String infoType;
    /**
     * info
     */
    private String info;
    /**
     * 创建日期
     */
    private LocalDateTime gmtCreate;
    /**
     * 修改日期
     */
    private LocalDateTime gmtModified;
}
