package com.yupi.usercenter.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yupi.usercenter.contant.UserConstant;
import com.yupi.usercenter.mapper.UserMapper;
import com.yupi.usercenter.model.domain.User;
import com.yupi.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author: wy
 * @CreateTime: 2023-11-26  01:40
 * @Description: TODO
 * @Version: 1.0
 */
@Component
@Slf4j
public class PreCacheJob {
    @Resource
    private UserMapper userMapper;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    @Resource
    private UserService userService;
    @Resource
    private RedissonClient redissonClient;

    private final List<Long> ids = List.of(1L);

//    @Scheduled(cron = "0 0 0 ? * ? ")
//    public void doPreCommendsUsers() {
//        ObjectMapper objectMapper = new ObjectMapper();
//        for (Long userId : ids) {
//            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
//            wrapper.eq(User::getUserStatus, UserConstant.USER_ENABLE);
//            Page<User> userPage = this.userMapper.selectPage(new Page<>(1, 20), wrapper);
//            List<User> records = userPage.getRecords();
//            if (records.isEmpty()) {
//                new Page<>();
//            }
//            List<User> safeUserPage = records.stream().map(user -> this.userService.getSafetyUser(user)).collect(Collectors.toList());
//            userPage.setRecords(safeUserPage);
//            try {
//                String redisValue = objectMapper.writeValueAsString(userPage);
//                String redisKey = String.format("yupao:user:recommendsUsers:%s", userId);
//                redisTemplate.opsForValue().set(redisKey,redisValue,30, TimeUnit.MINUTES);
//            } catch (JsonProcessingException e) {
//                log.error(e.getMessage());
//            }
//        }
//    }

    @Scheduled(cron = "0 0 0 ? * ? ")
    public void doPreCommendsUserByRedisLock() {
        RLock lock = redissonClient.getLock("yupao:preCacheJob:doCache:lock");
        try {
            if (lock.tryLock(0,30000, TimeUnit.MILLISECONDS)) {
                ObjectMapper objectMapper = new ObjectMapper();
                for (Long userId : ids) {
                    LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
                    wrapper.eq(User::getUserStatus, UserConstant.USER_ENABLE);
                    Page<User> userPage = this.userMapper.selectPage(new Page<>(1, 20), wrapper);
                    List<User> records = userPage.getRecords();
                    if (records.isEmpty()) {
                        new Page<>();
                    }
                    List<User> safeUserPage = records.stream().map(user -> this.userService.getSafetyUser(user)).collect(Collectors.toList());
                    userPage.setRecords(safeUserPage);
                    try {
                        String redisValue = objectMapper.writeValueAsString(userPage);
                        String redisKey = String.format("yupao:user:recommendsUsers:%s", userId);
                        redisTemplate.opsForValue().set(redisKey,redisValue,30, TimeUnit.MINUTES);
                    } catch (JsonProcessingException e) {
                        log.error("doPreCache JSON error",e);
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("doPreCache lock error",e);
        } finally {
            // 是否是当前线程 避免释放错误
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

}
