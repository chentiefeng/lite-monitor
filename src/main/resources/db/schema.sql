create table if not exists lite_monitor_config
(
    id           bigint(20)     auto_increment       NOT NULL COMMENT 'ID',
    monitor_type varchar(16)          not null comment '监控类型，进程：PROCESS，日志：LOG',
    frequency    varchar(32)          not null comment '频率，cron表达式或者枚举',
    schema_name  varchar(64)          null comment 'schema',
    host_name    varchar(16)          null comment '主机',
    username     varchar(32)          null comment '用户名',
    pwd          varchar(256)         null comment '密码',
    pem          text                 null comment '密钥',
    port         bigint               null comment '端口',
    file_path    varchar(512)         null comment '文件地址',
    stat_second  bigint               null comment '统计范围，秒',
    threshold    bigint               null comment '阈值',
    script    text         null comment '脚本',
    ding_type     varchar(64)          null comment '提醒类型',
    ding_title   varchar(64)          null comment '钉钉标题',
    ding_token   varchar(255)          null comment '钉钉token',
    sign_key   varchar(64)          null comment '钉钉签名Key',
    ding_at      varchar(256)         null comment '钉钉at',
    show_count   tinyint              null comment '钉钉展示条数',
    remark       varchar(256)         null comment '备注',
    enabled      tinyint(2) default 0 null comment '是否启用，0未启用，1启用',
    gmt_create   datetime             null comment '创建日期',
    gmt_modified datetime             null comment '修改日期',
        PRIMARY KEY (`id`)
);

create index IF NOT EXISTS lite_monitor_config_frequency_index_frequency
    on lite_monitor_config (frequency);

create index IF NOT EXISTS lite_monitor_config_frequency_index_host
    on lite_monitor_config (host_name);

