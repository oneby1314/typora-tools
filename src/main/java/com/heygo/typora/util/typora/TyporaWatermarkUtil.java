package com.heygo.typora.util.typora;

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
public class TyporaWatermarkUtil {

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

        StringBuffer sb = new StringBuffer();

        try {
            // 如果找到了图片链接，直接干他
            while (matcher.find()) {
                // group0 是整个正则表达式，也就是说 () 匹配的 group 编号从 1 开始
                String picUrl = matcher.group(2);

                // 检查图片是否已经是网络 URL 引用，如果已经是网络 URL 引用，则不需做任何操作
                Boolean isGif = picUrl.contains("gif");

                // 不对 Gif 做任何处理
                if (isGif == false) {
                    matcher.appendReplacement(sb, "$1" + "$2" + OSSConfig.getOSSConfig().getWaterMarkParams() + ")");
                }

            }
            // 添加上剩余部分并返回
            matcher.appendTail(sb);
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}
