package com.wy.TongZhi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * 启动类
 */
@SpringBootApplication
@MapperScan("com.wy.TongZhi.mapper")
@EnableScheduling
public class TongZhiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TongZhiApplication.class, args);
    }

}