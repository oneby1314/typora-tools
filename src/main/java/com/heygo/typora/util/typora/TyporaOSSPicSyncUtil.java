package com.heygo.typora.util.typora;

import com.Entity.ResultEntity;
import com.heygo.typora.config.OSSConfig;
import com.heygo.typora.util.common.AliyunOSSUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName TyporaOSSPicSyncUtil
 * @Description TODO
 * @Author Heygo
 * @Date 2020/7/24 12:44
 * @Version 1.0
 */
public class TyporaOSSPicSyncUtil {

    /**
     * 执行单个 md 文件的图片同步
     *
     * @param allPicFiles   本地图片的 File 对象数组
     * @param mdFileContent md 文件的内容
     * @return 图片同步之后，md 文件的内容（已经将本地图片链接替换为网路链接）
     */
    public static String doSingleMdPicSyncToOSS(File[] allPicFiles, String mdFileContent) {
        // 如果本地图片都没有，还同步个鸡儿
        if (allPicFiles == null) {
            return mdFileContent;
        }
        return changeLocalReferToUrlRefer(allPicFiles, mdFileContent);
    }

    /**
     * 执行单个 md 文件的图片同步
     *
     * @param allPicFiles   本地图片的 File 对象数组
     * @param mdFileContent md 文件的内容
     * @return 图片同步之后，md 文件的内容（已经将本地图片链接替换为网路链接）
     */
    private static String changeLocalReferToUrlRefer(File[] allPicFiles, String mdFileContent) {

        // 存储md文件内容
        StringBuffer sb = new StringBuffer();

        // 当前行内容
        String curLine;

        // 获取所有本地图片的名称
        List<String> allPicNames = new ArrayList<>();
        for (File curPicFile : allPicFiles) {
            // 获取图片名称
            String curPicName = curPicFile.getName();
            // 添加至集合中
            allPicNames.add(curPicName);
        }

        // 图片路径存储格式：![image-20200711220145723](https://heygo.oss-cn-shanghai.aliyuncs.com/Software/Typora/Typora_PicGo_CSDN.assets/image-20200711220145723.png)
        // 正则表达式
        /*
            \[.*\]：![image-20200711220145723]
                . ：匹配任意字符
                * ：出现0次或多次
            \(.+\)：(https://heygo.oss-cn-shanghai.aliyuncs.com/Software/Typora/Typora_PicGo_CSDN.assets/image-20200711220145723.png)
                . ：匹配任意字符
                + ：出现1次或多次
            (!\[.*\]) 为 $1，\(.+\) 为 $2
         */
        String regex = "(!\\[.*\\])\\((.+)\\)";

        // 执行正则表达式
        Matcher matcher = Pattern.compile(regex).matcher(mdFileContent);

        try {
            // 如果找到了图片链接，直接干他
            while (matcher.find()) {
                // group0 是整个正则表达式，也就是说 () 匹配的 group 编号从 1 开始
                String picUrl = matcher.group(2);

                // 检查图片是否已经是网络 URL 引用，如果已经是网络 URL 引用，则不需做任何操作
                Boolean isOSSUrl = picUrl.contains("http://") || picUrl.contains("https://");
                if (!isOSSUrl) {

                    // 获取图片名称
                    Integer picNameStartIndex = picUrl.lastIndexOf("/");
                    String picName = picUrl.substring(picNameStartIndex + 1, picUrl.length());

                    // 拿到 URl 在 List 中的索引
                    Integer picIndex = allPicNames.indexOf(picName);

                    // 如果图片是真实存在于本地磁盘上的
                    if (picIndex != -1) {

                        // 拿到待上传的图片 File 对象
                        File needUploadPicFile = allPicFiles[picIndex];

                        // 检查 OSS 上是否已经有该图片的 URL
                        String picOSSUrl = findPicOnOSS(needUploadPicFile);

                        // 在 OSS 上找不到才执行上传
                        if (picOSSUrl == "") {
                            // 执行上传
                            picOSSUrl = uploadPicToOSS(needUploadPicFile);
                        }

                        // 执行正则替换
                        matcher.appendReplacement(sb, "$1(" + picOSSUrl + ")");

                        // 打印输出日志
                        System.out.println("修改图片连接：" + picOSSUrl);
                    }
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

    /**
     * 上传图片至阿里云 OSS 服务器
     *
     * @param curPicFile 图片 File 对象
     * @return 图片上传后的 URL 地址
     */
    private static String uploadPicToOSS(File curPicFile) {

        // 目录名称，注意：不建议使用特殊符号和中文，会进行 URL 编码
        String folderName = "images";

        // 获取 OSS 配置信息
        OSSConfig ossConfig = OSSConfig.getOSSConfig();

        // 执行上传
        InputStream is = null;
        try {

            // 文件输入流
            is = new FileInputStream(curPicFile);

            // 文件名称
            String curFileName = curPicFile.getName();

            // 执行上传
            ResultEntity<String> resultEntity = AliyunOSSUtil.uploadFileToOss(
                    ossConfig.getEndPoint(),
                    ossConfig.getAccessKeyId(),
                    ossConfig.getAccessKeySecret(),
                    ossConfig.getBucketDomain(),
                    ossConfig.getBucketName(),
                    is,
                    folderName,
                    curFileName
            );
            if (ResultEntity.SUCCESS.equals(resultEntity.getResult())) {
                // 上传成功：URL 格式：http://heygo.oss-cn-shanghai.aliyuncs.com/images/2020-07-12_204547.png
                String url = resultEntity.getData();
                return url;
            } else {
                // 上传失败，返回空串
                System.out.println(resultEntity.getMessage());
                return "";
            }
        } catch (FileNotFoundException e) {
            // 发生异常也返回空串
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 在 OSS 服务器上查找是否存在目标图片
     *
     * @param curPicFile 目标图片
     * @return 图片的网络路径：如果为""，则表示图片不存在于 OSS 服务器上；如果不为""，则表示图片在 OSS 上的路径
     */
    private static String findPicOnOSS(File curPicFile) {

        // 获取 OSS 配置信息
        OSSConfig ossConfig = OSSConfig.getOSSConfig();

        // 目录名称
        String folderName = "images";

        // 获取文件路径
        String fileName = curPicFile.getName();

        // 判断是否存在于 OSS 中
        Boolean isExist = AliyunOSSUtil.isFileExitsOnOSS(
                ossConfig.getEndPoint(),
                ossConfig.getAccessKeyId(),
                ossConfig.getAccessKeySecret(),
                ossConfig.getBucketName(),
                folderName,
                fileName
        );

        // 不存在返回空串
        if (!isExist) {
            return "";
        }

        // 拼接图片 URL 并返回
        String bucketDomain = ossConfig.getBucketDomain();
        String picUrl = bucketDomain + "/images/" + fileName;
        return picUrl;
    }
}
