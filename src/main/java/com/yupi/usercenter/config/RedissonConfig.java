package com.yupi.usercenter.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @Author: wy
 * @CreateTime: 2023-11-27  23:34
 * @Description: TODO
 * @Version: 1.0
 */
@Configuration
@Data
@ConfigurationProperties(prefix = "spring.redis")
@Slf4j
public class RedissonConfig {
    private String host;
    private int port;
    private String password;
    private int database;
    @Bean
    public RedissonClient redissonClient() {
        log.info("host: {},port:{},password:{}",host,port,password);
        // 1. Create config object
        String redisConnect = String.format("redis://%s:%s", host, port);
        Config config = new Config();
        config.useSingleServer()
                .setPassword(password)
                .setAddress(redisConnect)
                .setDatabase(database);
        return Redisson.create(config);
    }
}
