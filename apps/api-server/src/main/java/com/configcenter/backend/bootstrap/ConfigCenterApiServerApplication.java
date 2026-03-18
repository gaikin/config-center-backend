package com.configcenter.backend.bootstrap;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.configcenter.backend")
@MapperScan({
        "com.configcenter.backend.module",
        "com.configcenter.backend.infrastructure.db"
})
public class ConfigCenterApiServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigCenterApiServerApplication.class, args);
    }
}
