package com.heygo.typora.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * @ClassName TyporaToolConfig
 * @Description TODO
 * @Author Heygo
 * @Date 2020/7/25 13:47
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TyporaToolConfig {

    private  boolean isNeedCleanPic; // 是否需要进行图片清理
    private  boolean isNeedTiltleAutoNo; // 是否需要进行标题编号
    private  boolean isNeedPicSyncOSS; // 是否需要进行图片同步
    private  boolean isNeedWaterMark; // 是否需要为图片添加水印
    private  String noteRootPath; // 笔记的根目录（也可以填入单个的 .md 文件）

    // 单例对象
    private static TyporaToolConfig typoraToolConfig;

    // 使用静态代码块初始化，保证线程安全
    static {
        // 创建单例对象
        typoraToolConfig = new TyporaToolConfig();
        // 填充属性
        Properties prop = new Properties();
        try (
                InputStream is = TyporaToolConfig.class.getClassLoader().getResourceAsStream("typora-tool.properties");
        ){
            // 指定 "UTF-8" 编码解决乱码问题
            prop.load(new InputStreamReader(is, "UTF-8"));
            typoraToolConfig.isNeedCleanPic = Boolean.parseBoolean(prop.getProperty("isNeedCleanPic"));
            typoraToolConfig.isNeedTiltleAutoNo = Boolean.parseBoolean(prop.getProperty("isNeedTiltleAutoNo"));
            typoraToolConfig.isNeedPicSyncOSS = Boolean.parseBoolean(prop.getProperty("isNeedPicSyncOSS"));
            typoraToolConfig.isNeedWaterMark = Boolean.parseBoolean(prop.getProperty("isNeedWaterMark"));
            typoraToolConfig.setNoteRootPath(prop.getProperty("noteRootPath"));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("配置文件有点问题，你去检查下哦~~~");
        }
    }

    // 返回单例的配置对象
    public static TyporaToolConfig getTyporaToolConfig(){
        return typoraToolConfig;
    }

}
