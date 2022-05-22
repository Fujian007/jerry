package com.xu.Jerry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@SpringBootApplication
@ServletComponentScan
@EnableTransactionManagement//开启事务
public class JerryApplication {
    public static void main(String[] args) {
        SpringApplication.run(JerryApplication.class,args);
        log.debug("项目启动...");
    }
}
