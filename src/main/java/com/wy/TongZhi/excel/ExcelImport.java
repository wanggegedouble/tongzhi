package com.wy.TongZhi.excel;

import com.alibaba.excel.EasyExcel;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @Author: wy
 * @CreateTime: 2023-11-22  23:49
 * @Description: TODO
 * @Version: 1.0
 */
@Slf4j
public class ExcelImport {
    public static void main(String[] args) {
        // 写法1：JDK8+ ,不用额外写一个DemoDataListener
        String fileName = "D:\\_CODE\\Java\\TongZhi\\UserInfo.xlsx";
        List<UserInfo> userInfos = EasyExcel.read(fileName).head(UserInfo.class).sheet().doReadSync();
        System.out.println(userInfos);
    }
}
