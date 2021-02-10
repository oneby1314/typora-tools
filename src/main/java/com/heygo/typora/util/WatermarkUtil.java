package com.heygo.typora.util;

import com.heygo.typora.config.OSSConfig;
import com.heygo.typora.config.TyporaToolConfig;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName OSSWaterMarkUtil
 * @Description TODO
 * @Author Oneby
 * @Date 2021/2/5 19:00
 * @Version 1.0
 */
public class WatermarkUtil {

    /*** 
     * @description: 给 md 文件中的图片加上水印
     * @param: originMdContent 不带图片水印的 md 文件内容
     * @return: java.lang.String 加上图片水印后的 md 文件内容
     * @author Oneby
     * @date: 22:18 2021/2/5
     */
    public static String getWaterMarkMdContent(String originMdContent) {
        // 图片路径存储格式：![image-20200711220145723](https://heygo.oss-cn-shanghai.aliyuncs.com/Software/Typora/Typora_PicGo_CSDN.assets/image-20200711220145723.png)
        // 正则表达式
        /*
            \[.*\]：![image-20200711220145723]
                . ：匹配任意字符
                * ：出现0次或多次
            \(.+\)：(https://heygo.oss-cn-shanghai.aliyuncs.com/Software/Typora/Typora_PicGo_CSDN.assets/image-20200711220145723.png)
                . ：匹配任意字符
                + ：出现1次或多次
            (!\[.*\]) 为 $1，(\(.+) 为 $2
         */
        String regex = "(!\\[.*\\])(\\(.+)\\)";
        // 执行正则表达式
        Matcher matcher = Pattern.compile(regex).matcher(originMdContent);
        String waterMarkMdContent = matcher.replaceAll("$1" + "$2" + OSSConfig.getOSSConfig().getWaterMarkParams() + ")");

        return waterMarkMdContent;
    }

}
