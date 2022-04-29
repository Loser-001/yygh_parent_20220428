package com.atguigu.yygh.oss.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author Jerry
 * @create 2022-03-03 21:54
 */
public interface FileService {
    //文件上传
    String upload(MultipartFile file);
}
