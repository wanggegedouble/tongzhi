package com.yupi.usercenter.service;

import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @Author: wy
 * @CreateTime: 2023-11-27  23:42
 * @Description: TODO
 * @Version: 1.0
 */
@SpringBootTest
public class RedissonTest {
    @Resource
    private RedissonClient redissonClient;

    @Test
    public void test() {
        RList<String> list = redissonClient.getList("test-list");
        list.add("wu");
        list.add("hk");
        list.add("h");
        System.out.println(list);
    }
}
