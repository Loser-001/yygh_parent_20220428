package com.atguig.easyExcel;

import com.alibaba.excel.EasyExcel;

import javax.activation.MailcapCommandMap;

/**
 * @author Jerry
 * @create 2022-02-21 19:06
 */
public class TestRead {
    public static void main(String[] args) {
        //读取文件路径
        String fileName = "D:\\Document\\JAVA\\JAVA_develop\\Java_Workspace\\Java_MyWorkspace\\hospitalProject\\excel\\01.xlsx";
        //调用方法实现读取操作
        EasyExcel.read(fileName,UserData.class,new ExcelListener()).sheet().doRead();
    }
}
