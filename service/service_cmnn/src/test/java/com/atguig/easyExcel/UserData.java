package com.atguig.easyExcel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author Jerry
 * @create 2022-02-21 16:42
 */

@Data
public class UserData {
    @ExcelProperty(value = "用户编号",index = 0)//index = 0 表示从第一列开始
    private int uid;

    @ExcelProperty(value = "用户名称",index = 1) //index = 1 表示从第二列开始
    private String username;

}
