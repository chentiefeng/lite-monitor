package me.ctf.lm.service.impl.distributetask;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import me.ctf.lm.dao.LiteMonitorExecSupportInfoRepository;
import me.ctf.lm.dto.MapResult;
import me.ctf.lm.entity.LiteMonitorExecSupportInfoEntity;
import me.ctf.lm.enums.HostStateEnum;
import me.ctf.lm.service.DistributeTaskService;
import me.ctf.lm.util.MonitorConfigUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: chentiefeng[chentiefeng@linzikg.com]
 * @create: 2019-12-18 18:01
 */
@ConditionalOnExpression(value = "'${monitor.distribute-task-type:http}'.equalsIgnoreCase('http')")
@Service("httpDistributeTaskService")
@Slf4j
public class HttpDistributeTaskServiceImpl implements DistributeTaskService<Long[]>, CommandLineRunner {
    public static final String EXECUTE_URL = "%s/liteMonitor/execute";
    /**
     * 每隔30秒检查一次
     */
    public static final int CHECK_DURATION_SECOND = 60;
    private static Gson gson = new GsonBuilder().create();
    private static RestTemplate restTemplate = new RestTemplateBuilder().setConnectTimeout(Duration.ofMillis(3000)).build();
    @Resource
    private LiteMonitorExecSupportInfoRepository liteMonitorExecSupportInfoRepository;
    @Value("${server.port}")
    private Integer port;
    private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(), new ThreadFactoryBuilder().setNameFormat("thread-register-host-%s").build());
    /**
     * support info type
     */
    private final static String HOST = "HOST";
    /**
     * host idx
     */
    private AtomicInteger idx;

    @Override
    public void distribute(Long[] ids) {
        try {
            log.info("start distribute task.");
            LocalDateTime localDateTime = LocalDateTime.now().plusMinutes(-MonitorConfigUtil.getDuration());
            Date gmtModified = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
            log.info("ids is {}, duration is {}, gmtModified is {}", Arrays.toString(ids), MonitorConfigUtil.getDuration(), gmtModified);
            List<LiteMonitorExecSupportInfoEntity> list = liteMonitorExecSupportInfoRepository.findByInfoTypeAndGmtModifiedGreaterThanEqual(HOST, gmtModified);
            String[] hosts = list.stream().map(LiteMonitorExecSupportInfoEntity::getInfo).toArray(String[]::new);
            log.info("distribute host list {}", Arrays.toString(hosts));
            if (hosts.length == 0) {
                return;
            }
            if (idx == null) {
                idx = new AtomicInteger((int) (System.currentTimeMillis() % hosts.length));
            }
            String host = hosts[idx.getAndIncrement() % hosts.length];
            log.info("This distribution task host is {}", host);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json;charset=UTF-8");
            HttpEntity<String> r = new HttpEntity<>(gson.toJson(ids), headers);
            restTemplate.postForObject(String.format(EXECUTE_URL, "http://" + host + ":" + port), r, MapResult.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            log.info("end distribute task.");
        }
    }

    @Override
    public void run(String... args) {
        threadPoolExecutor.execute(this::registerHost);
    }

    /**
     * 注册本机ip
     */
    private void registerHost() {
        if (MonitorConfigUtil.getCluster()) {
            String host = getHost();
            log.info("Http distribute task service register host {}", host);
            do {
                LiteMonitorExecSupportInfoEntity supportInfo = liteMonitorExecSupportInfoRepository.findByInfoTypeAndInfo(HOST, host);
                if (Objects.isNull(supportInfo)) {
                    supportInfo = new LiteMonitorExecSupportInfoEntity();
                    supportInfo.setInfoType(HOST);
                    supportInfo.setInfo(host);
                    supportInfo.setGmtCreate(new Date());
                }
                supportInfo.setGmtModified(new Date());
                liteMonitorExecSupportInfoRepository.save(supportInfo);
                //每1秒检查一次
                try {
                    TimeUnit.SECONDS.sleep(CHECK_DURATION_SECOND);
                } catch (Exception e) {
                    log.info(e.getMessage(), e);
                }
            } while (HostStateEnum.ONLINE.name().equalsIgnoreCase(MonitorConfigUtil.getHostState()));
        }
    }

    /**
     * 获取ip地址
     *
     * @return
     */
    private String getHost() {
        String host = "localhost";
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                NetworkInterface net = en.nextElement();
                Enumeration<InetAddress> address = net.getInetAddresses();
                while (address.hasMoreElements()) {
                    InetAddress inetAddress = address.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress() && inetAddress.isSiteLocalAddress()) {
                        host = inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException var4) {
            host = "localhost";
        }
        return host;
    }

}
