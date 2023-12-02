package com.yupi.usercenter;

import com.yupi.usercenter.model.domain.User;
import com.yupi.usercenter.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 启动类测试
 */
@SpringBootTest
class UserCenterApplicationTests {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    private final ExecutorService executorService = new ThreadPoolExecutor(20, 1000, 10000, TimeUnit.MINUTES, new ArrayBlockingQueue<>(10000));


    // https://yupi.icu/

    @Test
    void testDigest() throws NoSuchAlgorithmException {
        String newPassword = DigestUtils.md5DigestAsHex(("abcd" + "mypassword").getBytes());
        System.out.println(newPassword);
        int a = 10;
    }


    @Test
    void contextLoads() {

    }

    @Test
    public void doConcurrencyInsertUsers() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // 分十组
        int batchSize = 25000;
        int j = 0;
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            List<User> userList = new ArrayList<>();
            do {
                j++;
                User user = new User();
                user.setUsername("假鱼皮"+j);
                user.setUserAccount("fakeyupi");
                user.setAvatarUrl("https://636f-codenav-8grj8px727565176-1256524210.tcb.qcloud.la/img/logo.png");
                user.setGender(0);
                user.setUserPassword("12345678");
                user.setPhone("123");
                user.setEmail("123@qq.com");
                user.setTags("[]");
                user.setUserStatus(0);
                user.setUserRole(0);
                user.setPlanetCode("11111111");
                userList.add(user);
            } while ((j % batchSize) != 0);
            // 异步执行
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                System.out.println("threadName: " + Thread.currentThread().getName());
                userService.saveBatch(userList, batchSize);
            }, executorService);
            futureList.add(future);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
        // 20 秒 10 万条
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }

    @Test
    void RedisTest() {
        redisTemplate.opsForValue().set("wy","wangyao");
        String name = (String)redisTemplate.opsForValue().get("wy");
        System.out.println(name);
        redisTemplate.opsForValue().set("hk", 2);
        System.out.println(redisTemplate.opsForValue().get("hk"));
    }

}

// [编程知识星球](https://yupi.icu) 连接万名编程爱好者，一起优秀！20000+ 小伙伴交流分享、100+ 各方向编程交流群、40+ 大厂嘉宾一对一答疑、4000+ 编程问答参考