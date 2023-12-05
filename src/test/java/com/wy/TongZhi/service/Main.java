package com.wy.TongZhi.service;

import com.wy.TongZhi.model.domain.User;

import java.util.Date;

/**
 * @Author: wy
 * @CreateTime: 2023-12-04  20:33
 * @Description: TODO
 * @Version: 1.0
 */
public class Main {
    public static void main(String[] args) {
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
        user02.setId(0L);
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
