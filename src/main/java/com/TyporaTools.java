package com;

import com.heygo.typora.config.TyporaToolConfig;
import com.heygo.typora.util.common.ClipboardUtil;
import com.heygo.typora.util.typora.TyporaWatermarkUtil;
import com.heygo.typora.util.typora.TyporaFileRwUtil;
import com.heygo.typora.util.typora.TyporaOSSPicSyncUtil;
import com.heygo.typora.util.typora.TyporaPicCleanUtil;
import com.heygo.typora.util.typora.TyporaTiltleAutoNoUtil;

import java.io.*;

/**
 * @ClassName TyporaTools
 * @Description TODO
 * @Author Heygo
 * @Date 2020/7/24 11:44
 * @Version 1.0
 */
public class TyporaTools {
    public static void main(String[] args) {

        // 笔记存储根目录
        String noteRootPath = null;

        if (args != null && args.length > 0) {
            noteRootPath = args[0];
            System.out.println(noteRootPath);
        } else {
            noteRootPath = TyporaToolConfig.getTyporaToolConfig().getNoteRootPath();
        }


        // 根据配置需要，执行 Typora 文件瘦身、标题自动编号、图片同步至 OSS 等功能
        doMainBusiness(noteRootPath);

    }

    private static void doMainBusiness(String destPath) {

        // 获取当前路径的File对象
        File destPathFile = new File(destPath);

        // 如果是文件，执行单个md文件的图片瘦身，然后递归返回即可
        if (destPathFile.isFile()) {
            doSingleMdMainBusiness(destPathFile);
            return;
        }

        // 获取当前路径下所有的子文件和路径
        File[] allFiles = destPathFile.listFiles();

        // 遍历allFiles
        for (File curFile : allFiles) {

            // 获取curFile对象是否为文件夹
            Boolean isDirectory = curFile.isDirectory();

            // 获取当前curFile对象对应的绝对路径名
            String absolutePath = curFile.getAbsolutePath();

            // 如果是文件夹
            if (isDirectory) {
                // 如果是asset文件夹，则直接调过
                if (absolutePath.endsWith(".assets")) {
                    continue;
                }
            }

            // 如果是文件夹，则继续执行递归
            doMainBusiness(absolutePath);
        }

    }

    private static void doSingleMdMainBusiness(File destMdFile) {
        // 如果不是 MD 文件，滚蛋
        Boolean isMdFile = destMdFile.getName().endsWith(".md");
        if (!isMdFile) {
            return;
        }

        // 读取 MD 文件内容
        String mdFileContent = TyporaFileRwUtil.readMdFileContent(destMdFile);

        // 获取当前 MD 文件的图片 File 对象数组
        File[] allPicFiles = TyporaPicCleanUtil.getLocalPicFiles(destMdFile);

        // 获取配置文件内容
        TyporaToolConfig typoraToolConfig = TyporaToolConfig.getTyporaToolConfig();

        // Typora 瘦身
        if (typoraToolConfig.isNeedCleanPic()) {
            System.out.println(destMdFile.getName() + " 开始执行瘦身计划...");
            TyporaPicCleanUtil.doSingleTyporaClean(allPicFiles, mdFileContent);
            System.out.println(destMdFile.getName() + " 瘦身计划执行完毕~~~");
            System.out.println();
        }

        // 标题自动标号
        if (typoraToolConfig.isNeedTiltleAutoNo()) {
            System.out.println(destMdFile.getName() + " 开始执行标题自动标号...");
            mdFileContent = TyporaTiltleAutoNoUtil.doSingleMdTitleAutoNo(mdFileContent);
            System.out.println(destMdFile.getName() + " 标题自动标号完成~~~");
            System.out.println();
        }

        // 图片同步至阿里云 OSS
        if (typoraToolConfig.isNeedPicSyncOSS()) {
            System.out.println(destMdFile.getName() + " 开始执行图片同步至 OSS...");
            mdFileContent = TyporaOSSPicSyncUtil.doSingleMdPicSyncToOSS(allPicFiles, mdFileContent);
            System.out.println(destMdFile.getName() + " 图片同步至 OSS 完成~~~");
            System.out.println();
        }

        // 执行保存
        TyporaFileRwUtil.SaveMdContentToFile(destMdFile.getPath(), mdFileContent);

        // 获取图片带水印的 Markdown 文档
        if (TyporaToolConfig.getTyporaToolConfig().isNeedWaterMark() == true) {
            System.out.println(destMdFile.getName() + " 开始添加图片水印~~~");
            mdFileContent = TyporaWatermarkUtil.getWaterMarkMdContent(mdFileContent);
            System.out.println(destMdFile.getName() + " 图片水印添加完毕~~~");
            System.out.println();
        }

        // 将笔记内容保存至粘贴板
        ClipboardUtil.setClipboardString(mdFileContent);
        System.out.println(destMdFile.getName() + " 已经复制到粘贴板");
    }


}
