package me.ctf.lm.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author: chentiefeng[chentiefeng@linzikg.com]
 * @create: 2019-12-18 14:54
 */
@Entity
@Data
@Table(name = "lite_monitor_exec_support_info")
public class LiteMonitorExecSupportInfoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private Date gmtCreate;
    /**
     * 修改日期
     */
    private Date gmtModified;
}
