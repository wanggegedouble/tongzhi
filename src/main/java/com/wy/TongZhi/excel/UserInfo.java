package com.wy.TongZhi.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Author: wy
 * @CreateTime: 2023-11-22  23:50
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class UserInfo {
    @ExcelProperty({"UserInfo","姓名"})
    private String name;
    @ExcelProperty({"UserInfo","年龄"})
    private int age;
    @ExcelProperty({"UserInfo","成绩"})
    private double score;
    @ExcelProperty({"UserInfo","时间"})
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;
}
