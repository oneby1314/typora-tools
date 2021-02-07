package com.heygo.typora.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * @ClassName OSSConfig
 * @Description TODO
 * @Author Heygo
 * @Date 2020/7/25 13:47
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OSSConfig {

    // 连接阿里云 OSS 所需要的配置信息
    private String endPoint;
    private String bucketName;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketDomain;

    // 图片水印参数
    private String waterMarkParams;

    // 单例对象
    private static OSSConfig ossConfig;

    // 使用静态代码块初始化，保证线程安全
    static {
        // 创建单例对象
        ossConfig = new OSSConfig();
        // 填充属性
        Properties prop = new Properties();
        try (
            InputStream is = TyporaToolConfig.class.getClassLoader().getResourceAsStream("typora-tool.properties");
        ){
            // 指定 "UTF-8" 编码解决乱码问题
            prop.load(new InputStreamReader(is, "UTF-8"));
            ossConfig.setEndPoint(prop.getProperty("endPoint"));
            ossConfig.setBucketName(prop.getProperty("bucketName"));
            ossConfig.setAccessKeyId(prop.getProperty("accessKeyId"));
            ossConfig.setAccessKeySecret(prop.getProperty("accessKeySecret"));
            ossConfig.setBucketDomain(prop.getProperty("bucketDomain"));
            ossConfig.setWaterMarkParams(prop.getProperty("waterMarkParams"));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("配置文件有点问题，你去检查下哦~~~");
        }
    }

    // 返回单例的配置对象
    public static OSSConfig getOSSConfig(){
        return ossConfig;
    }

}
