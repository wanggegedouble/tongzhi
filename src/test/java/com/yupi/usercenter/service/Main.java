package com.yupi.usercenter.service;

import org.apache.commons.codec.digest.Md5Crypt;
import org.springframework.util.DigestUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * @Author: wy
 * @CreateTime: 2023-11-22  00:22
 * @Description: TODO
 * @Version: 1.0
 */
public class Main {
    public static void main(String[] args) {
        String encryptPassword = DigestUtils.md5DigestAsHex(("yupi" + "123456").getBytes());
        System.out.println(encryptPassword);
    }
}
