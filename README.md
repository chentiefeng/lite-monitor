<div align="center">  
	<img src="https://chen_tiefeng.gitee.io/cloudimg/img/1576853070_799768.png" width = "500" height = "100" />
</div>
<div align="center">
<a href="https://github.com/chentiefeng/lite-monitor"><img src="https://img.shields.io/badge/lm-lite--monitor-brightgreen.svg" /></a> <a href="https://chentiefeng.top"><img src="https://img.shields.io/badge/blog-chentiefeng-yellowgreen" /></a>
</div>

## 介绍

`lite-monitor` 一款基于shell命令的监控系统，可以根据项目中输出的日志定时输出或者统计输出，并发送钉钉机器人报警消息。

`lite-monitor`能做什么：

 - 定时监控某个服务进程是否还存在，不存在则钉钉告警。
 - 定时统计近一段时间内具体日志文件中关键字出现的次数，并对次数做一个阈值比较，超出阈值则钉钉告警并输出日志。
 - 进阶监控（qps/计算效率等）可以根据`awk`等命令自定义实现。

 `lite-monitor`的特点：

 - 每个监控可配置不同钉钉群机器人，可配置@具体人或者@all
 - 对已有项目无任何入侵，不需要重启或者其他操作。
 - 可以单机版极简配置（服务器安装有Java就行），或者集群部署（除非监控很多，否则基本没有必要 :smile:）。

> 在使用或开发过程中有任何疑问都可[联系我](https://chentiefeng.top) 。

## Todo List

* [x] [集群模式支持分发监控任务]。
* [x] [增加可选择`redis/kafka`分发任务]。
* [ ] 根据监控的阀值可定制推送/触发接口。

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

### 配置监控

浏览器输入地址`http://xx.xx.xx.xx:10003/`打开主页。
![](https://chen_tiefeng.gitee.io/cloudimg/img/Clip_20191223_160005.png)

#### 进程监控

我要监控本地机器（测试方便）的indicator-service进程（本来就没有）。


新增。
![](https://chen_tiefeng.gitee.io/cloudimg/img/Clip_20191223_160658.png)


确认-立即执行（测试一下）。
![](https://chen_tiefeng.gitee.io/cloudimg/img/Clip_20191223_160917.png)


钉钉消息。
![](https://chen_tiefeng.gitee.io/cloudimg/img/ps-monitor.jpg)

#### 日志监控
我要监控本地机器（可以替换其他机器）的lite-monitor服务的近1分钟出现`frequency`关键字的日志数量，超过2个就报警，钉钉展示10条消息。

复制，改改信息。
![](https://chen_tiefeng.gitee.io/cloudimg/img/Clip_20191223_162355.png)


确认-立即执行（测试一下），钉钉消息。
![](https://chen_tiefeng.gitee.io/cloudimg/img/log-monitor.jpg)

#### 试用
[lite-monitor](https://lite-monitor.chentiefeng.top)，可以将钉钉机器人token改为自己的试用

## 字段说明

- 监控类型：日志/进程，主要是生成的命令不同。

- 监控频率：定时触发的频率，cron表达式。
> 如果需要新增删改可以在源码的`me.ctf.lm.enums.FrequencyEnum`枚举类中修改。

- ip地址：被监控的机器的IP地址。

- 端口：被监控的机器的ssh端口，默认22。

- 用户：被监控的机器可以ssh登录的用户名。

- **密码/密钥文件地址：两者填写一个即可，密钥文件需要提前上传到部署机器上。**
> 两者都填，优先使用密码登录ssh。
> 两者都为空，需要实现免密登录，将部署机器的ssh的id_rsa.pub内容复制到被监控机器的`~/.ssh/authorized_keys`，例子中我就做了这事。

- 日志文件：监控类型为日志的时候需要填写，日志的绝对路径。

- 统计范围：监控类型为日志的时候需要填写，单位秒，范围：触发时间点往前减去N秒至触发时间点。

- 阈值：监控类型为日志的时候需要填写，日志在统计范围内出现多少次就报警。

- 展示条数：监控类型为日志的时候需要填写，在钉钉消息里面展示的日志数量。

- **命令：监控类型为日志的时候需要填写，在系统生成的命令后面增加管道命令。**
> 系统生成的命令为：`grep -e '2019-12-23 16:55:[3-9][0-9]' -e '2019-12-23 16:55:2[7-9]' /root/lite-monitor-server/logs/m.logs`，在后面增加`grep frequency`，最后生成的命令为`grep -e '2019-12-23 16:55:[3-9][0-9]' -e '2019-12-23 16:55:2[7-9]' /root/lite-monitor-server/logs/m.logs|grep frequency`。
>> 时间的正则表达式生成参考：[时间正则](https://chentiefeng.top/2019/03/09/datetime-regex/)
> 这里也可以填写`awk`等命令，统计一些效率/QPS等，比如统计QPS`grep '计算结束'|awk '{sum += 1} END {if(sum>60){print "近1分钟服务请求数量:" sum; print "近1分钟服务QPS:" sum/60}}'`。![](https://chen_tiefeng.gitee.io/cloudimg/img/qps-monitor.jpg)

- **关键字：监控类型为进程的时候需要填写，进程的关键字。**
> 监控进程的命令`ps -ef|grep 'xx'|grep -v grep|awk '{print $2}'`，打印进程id，不存在则报警。

- 钉钉标题：钉钉消息Markdown格式的标题

- 钉钉机器人token：钉钉机器人token，[钉钉机器人官方教程](https://ding-doc.dingtalk.com/doc#/serverapi2/qf2nxq)

- 钉钉@人员：@人员的手机号码，多个以,分开
> @all的话直接输入all

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


