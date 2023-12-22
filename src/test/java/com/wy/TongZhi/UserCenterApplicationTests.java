package com.wy.TongZhi;
import java.util.Date;

import com.wy.TongZhi.model.domain.User;
import com.wy.TongZhi.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
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

    private final ExecutorService executorService = new ThreadPoolExecutor(
                    16, 1000, 10000,
                    TimeUnit.MINUTES,
                    new ArrayBlockingQueue<>(10000));


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
                User user = getUser(j);
                userList.add(user);
            } while ((j % batchSize) != 0);
            // 异步执行
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                userService.saveBatch(userList, batchSize);
            }, executorService);
            futureList.add(future);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
        // 20 秒 10 万条
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }

    private User getUser(int j) {
        User user = new User();
        user.setUsername("假鱼皮"+ j);
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
        return user;
    }

    @Test
    void RedisTest() {
        redisTemplate.opsForValue().set("wy","wangyao");
        String name = (String)redisTemplate.opsForValue().get("wy");
        System.out.println(name);
        redisTemplate.opsForValue().set("hk", 2);
        System.out.println(redisTemplate.opsForValue().get("hk"));
    }

    @Test
    void ObjectTest() {
        User user1 = new User();
        user1.setId(0L);
        user1.setUsername("");
        user1.setTags("");
        user1.setUserAccount("");
        user1.setAvatarUrl("");
        user1.setGender(0);
        user1.setUserPassword("");
        user1.setPhone("");
        user1.setEmail("");
        user1.setUserStatus(0);
        user1.setCreateTime(new Date());
        user1.setUpdateTime(new Date());
        user1.setIsDelete(0);
        user1.setUserRole(0);
        user1.setPlanetCode("");

        User user02 = new User();
        user02.setId(1L);
        user02.setUsername("");
        user02.setTags("");
        user02.setUserAccount("");
        user02.setAvatarUrl("");
        user02.setGender(0);
        user02.setUserPassword("");
        user02.setPhone("");
        user02.setEmail("");
        user02.setUserStatus(0);
        user02.setCreateTime(new Date());
        user02.setUpdateTime(new Date());
        user02.setIsDelete(0);
        user02.setUserRole(0);
        user02.setPlanetCode("");

        System.out.println(user1.equals(user02));

    }

}


