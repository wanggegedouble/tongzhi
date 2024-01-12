package com.wy.TongZhi;

import ch.qos.logback.classic.pattern.MessageConverter;
import org.apache.poi.ss.formula.ptg.AreaNPtg;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.JsonbHttpMessageConverter;
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