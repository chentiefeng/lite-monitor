package me.ctf.lm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class LiteMonitorApplication {

    public static void main(String[] args) {
        SpringApplication.run(LiteMonitorApplication.class, args);
    }

}
