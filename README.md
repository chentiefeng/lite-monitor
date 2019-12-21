<div align="center">  
	<img src="https://chen_tiefeng.gitee.io/cloudimg/img/1576853070_799768.png" width = "500" height = "100" />
</div>
<div align="center">
<a href="https://github.com/chentiefeng/lite-monitor"><img src="https://img.shields.io/badge/lm-lite--monitor-brightgreen.svg" /></a> <a href="https://chentiefeng.top"><img src="https://img.shields.io/badge/blog-chentiefeng-yellowgreen" /></a>
</div>

## 介绍

`lite-monitor` 一款基于日志文件和shell命令的监控系统，可以根据项目中输出的日志定时输出或者统计输出，并发送钉钉机器人报警消息。

`lite-monitor`的特点：

 - 每个监控可配置不同钉钉群机器人，可配置@具体人或者@all
 - 对已有项目无任何入侵，不需要重启或者其他操作。
 - 可以单机版极简配置（服务器安装有Java就行），或者集群部署（除非监控很多，否则基本没有必要 :smile:）。


> 在使用或开发过程中有任何疑问都可[联系我](https://chentiefeng.top) 。

## Todo List

* [x] [集群模式支持分发监控任务]。
* [x] [增加可选择`redis/kafka`分发任务]。
* [ ] 选择`redis`后缓存监控任务。
* [ ] 根据监控的阀值可定制推送/触发接口。
* [ ] 增加多种报警方式可选（短信、邮件等）。



## 流程图

![](https://chen_tiefeng.gitee.io/cloudimg/img/lite-monitor-flow.jpg)

### 流程说明
1. 定时任务触发。
2. 查询可触发任务（事先配置好），分批分发给集群内部其他应用。
3. 通过`ssh`方式执行配置的任务，返回结果和阈值比较。
4. 超过阈值的任务拼装钉钉消息发送。

### 系统架构说明
- `lite-monitor` 采用 `SpringBoot` 构建。
-  定时任务暂时直接通过`SpringBoot`的`Schedule`注册，增加分布式锁防止重复执行。
-  分发机制默认采用`Http`方式，可选`Redis`生产消费队列 或者 `Kafka`消息
-  基于`ganymed-ssh2`连接执行`Shell`命令。


## 快速启动

首先需要安装 `JDK1.8`或者以上并保证网络通畅。

### 打包
```shell
git clone https://github.com/chentiefeng/lite-monitor.git
cd lite-monitor
mvn -Dmaven.test.skip=true clean package
```

### 部署

```shell
mkdir ~/lite-monitor-server
cp target/lite-monitor-0.0.1-SNAPSHOT.jar ~/lite-monitor-server
cd ~/lite-monitor-server
nohup java -jar  lite-monitor-0.0.1-SNAPSHOT.jar 2>&1 &
```

> 日志文件位置：`~/lite-monitor-server/logs/m.log`。

### 监控配置

### 钉钉消息展示


## 配置项说明

配置文件位置：/lite-monitor/src/main/resources/application.yml

```yml
server:
  port: 10003

spring:
  #============== redis ===================
#  redis:
#    host: 172.16.157.239
#    port: 6379
#    timeout: 6000
#    password:
#    database: 3
#    pool:
#      max-active: 1000
#      max-wait: -1
#      max-idle: 10
#      min-idle: 5

  #============== kafka ===================
#  kafka:
#    bootstrap-servers: cm02:9092,cm03:9092,cm04:9092
#    producer:
#      key-serializer: org.apache.kafka.common.serialization.StringSerializer
#      value-serializer: org.apache.kafka.common.serialization.StringSerializer
#      acks: all
#    consumer:
#      group-id: lite-monitor
#      key-serializer: org.apache.kafka.common.serialization.StringSerializer
#      value-serializer: org.apache.kafka.common.serialization.StringSerializer
#      max-poll-records: 10
#    listener:
#      type: batch
#      concurrency: 4
#      ack-mode: MANUAL
#      missing-topics-fatal: false

  #============== dataSource ==================
  datasource:
#    driver-class-name: com.mysql.cj.jdbc.Driver
#    url: jdbc:mysql://localhost:3306/lite_monitor?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&useSSL=true
#    username: root
#    password: 123456
    driver-class-name: org.h2.Driver
    url: jdbc:h2:~/lite-monitor
    username: sa
    password: 123456
  jpa:
#    database: mysql
    database: h2
    hibernate:
      ddl-auto: update
    show-sql: false

monitor:
  #是否集群，集群模式请用MySQL,注释掉h2部分，会分发定时任务 false/true
  cluster: false
  #集群模式下，集群检查分钟数，3分钟没有反应则表明机器挂了
#  duration: 3
  #集群模式下，是否注册本机 online/offline
#  hostState: online
  #集群模式下分布式锁类型，db/redis，选择redis把redis的配置注释打开
#  distributed-lock-type: db
  #集群模式下分发类型，http/redis/kafka，选择其他需要把注释打开
#  distribute-task-type: http
```

> 默认配置端口10003，采用单机模式，数据库为`h2`

### 集群模式配置说明

- `monitor.cluster`：`true/false`是否集群模式，默认`false`。改为`true`开启集群模式，集群模式下下面的配置才会生效。
> 采用集群模式需把数据库改为`Mysql`，并执行脚本，脚本位置：`/lite-monitor/db.sql`

- `monitor.hostState`：`online/offline`是否上线应用。默认`online`，改为`offline`后本应用会从集群模式中退出，分发任务会忽略本应用。

- `monitor.duration`：应用检查时间，单位分钟，不支持小数。默认3分钟。

- `monitor.distributed-lock-type`：`db/redis`分布式锁类型，默认`db`。`redis`类型支持锁过期设置、可重入锁，`db`类型均不支持。改为`redis`需要增加对应配置。
> db类型重启应用后应检查和删除数据`select * from lite_monitor_exec_support_info where info_type = 'LOCK' `

- `monitor.distribute-task-type`：`http/redis/kafka`分发任务类型，默认`http`。`http`类型为每10个任务分发一次，分发路由默认顺序分发，改为`redis`或者`kafka`需要增加对应配置。
> `redis`分发类型：基于`List`数据结构和`lpush/brpop`命令的生产者消费者模式。
> `kafka`分发类型：逐条发送消息，批量消费（默认一次`poll`10条消息）。

## 联系作者
- [chentiefeng@aliyun.com](mailto:chentiefeng@aliyun.com)
- ![](https://chen_tiefeng.gitee.io/cloudimg/img/wx_chentiefeng.jpg)

