package com.yupi.usercenter.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.data.*;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @Author: wy
 * @CreateTime: 2023-11-23  01:17
 * @Description: TODO
 * @Version: 1.0
 */
public class WriteExcel {
    public static void main(String[] args) {
        String filePath = "D:\\_CODE\\Java\\TongZhi\\User.xlsx";
        EasyExcel.write(filePath,UserInfo.class)
                .excelType(ExcelTypeEnum.XLSX)
                .sheet("UserInfo")
                .doWrite(getUerInfoData());

    }

    private static List<UserInfo> getUerInfoData() {
        List<UserInfo> list = new ArrayList<>();

        UserInfo userData1 = new UserInfo();
        userData1.setName("wy");
        userData1.setAge(12);
        userData1.setScore(23.7);
        userData1.setDate(new Date());

        list.add(userData1);

        UserInfo userData2 = new UserInfo();
        userData2.setName("何可");
        userData2.setAge(14);
        userData2.setScore(56.7);
        userData2.setDate(new Date());
        list.add(userData2);

        UserInfo userInfo = new UserInfo();
        userInfo.setName("qull");
        userInfo.setAge(12);
        userInfo.setScore(99);
        userInfo.setDate(new Date());
        return list;
    }
}
