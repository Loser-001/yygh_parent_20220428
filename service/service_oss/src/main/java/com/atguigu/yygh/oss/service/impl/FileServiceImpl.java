package com.atguigu.yygh.oss.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectRequest;
import com.atguigu.yygh.oss.service.FileService;
import com.atguigu.yygh.oss.utils.ConstantOssPropertiesUtil;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * @author Jerry
 * @create 2022-03-03 21:55
 */
@Service
public class FileServiceImpl implements FileService {
    @Override
    public String upload(MultipartFile file) {
        // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
        String endpoint = ConstantOssPropertiesUtil.ENDPOINT;
        // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
        String accessKeyId = ConstantOssPropertiesUtil.ACCESS_KEY_ID;
        String accessKeySecret = ConstantOssPropertiesUtil.SECRET;
        // 填写Bucket名称，例如examplebucket。
        String bucketName = ConstantOssPropertiesUtil.BUCKET;
        // 填写Object完整路径，例如exampledir/exampleobject.txt。Object完整路径中不能包含Bucket名称。
        String fileName = file.getOriginalFilename();
        //生成随机的一个值
        String uuid = UUID.randomUUID().toString().replaceAll("-","");
        fileName = uuid + fileName;
        //按照当前日期，创建文件夹，上传到创建的文件夹中去
        //  /2021/02/01.jpg
        String timeUrl = new DateTime().toString("yyyy/MM/dd");
        fileName = timeUrl + "/" + fileName;
        // 填写本地文件的完整路径，例如D:\\localpath\\examplefile.txt。
        // 如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件。
        String filePath= "D:\\localpath\\examplefile.txt";

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        InputStream inputStream = null;
        try {
            //上传文件流
             inputStream = file.getInputStream();
            // 创建PutObject请求。
            ossClient.putObject(bucketName, fileName, inputStream);
            //上传之后的文件路径
            //https://yygh-njupt001.oss-cn-beijing.aliyuncs.com/%E7%BE%8E%E5%A5%B32.jpg
            String url = "https://" + bucketName + "." + endpoint + "/" + fileName;
            System.out.println("url:" + url);
            return url;
        } catch (Exception ce) {
            ce.printStackTrace();
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        return null;
    }
}
