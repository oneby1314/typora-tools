package com.heygo.typora.util.typora;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName TyporaPicCleanUtil
 * @Description TODO
 * @Author Heygo
 * @Date 2020/7/24 11:47
 * @Version 1.0
 */
public class TyporaPicCleanUtil {

    /**
     * 执行单个 md 文件的图片瘦身
     *
     * @param allPicFiles   图片 File 对象（md 文件对应的 {filename}.assets 文件夹中的所有图片）
     * @param mdFileContent md 文件的内容
     */
    public static void doSingleTyporaClean(File[] allPicFiles, String mdFileContent) {

        // 获取文中所用到的所有图片名称
        String[] usedPicNames = getUsedPicNames(mdFileContent);

        // 删除无用图片
        CleanUnusedPic(allPicFiles, usedPicNames);
    }

    /**
     * 获取 md 文件对应的 {filename}.assets 文件夹中的所有图片
     *
     * @param destMdFile md 文件的 File 对象
     * @return 所有本地图片的 File 对象数组
     */
    public static File[] getLocalPicFiles(File destMdFile) {
        // 获取 MD 文件对应的 assets 文件夹
        // MD 文件所在目录
        String mdFileParentDir = destMdFile.getParent();
        // MD 文件名
        String mdFileName = destMdFile.getName();
        // 不带扩展名的 MD 文件名
        String mdFileNameWithoutExt = mdFileName.substring(0, mdFileName.lastIndexOf("."));
        // 拼接得到 assets 文件夹的路径
        String assetsAbsolutePath = mdFileParentDir + "\\" + mdFileNameWithoutExt + ".assets";
        // assets 目录的 File 对象
        File assetsFile = new File(assetsAbsolutePath);

        // 获取 assets 文件夹中的所有图片
        return assetsFile.listFiles();
    }

    /**
     * 获取 md 文件中使用到的图片名称
     *
     * @param mdFileContent md 文件的内容
     * @return 使用到的图片名称
     */
    private static String[] getUsedPicNames(String mdFileContent) {
        // 图片名称
        // 图片路径存储格式：![image-20200603100128164](IDEA快捷键.assets/image-20200603100128164.png)
        /*
            \[.*\]：[image-20200603100128164]
                . ：匹配任意字符
                * ：出现0次或多次
            \(.+\)：(IDEA快捷键.assets/image-20200603100128164.png)
                . ：匹配任意字符
                + ：出现1次或多次
         */
        String regex = "!\\[.*\\]\\(.+\\)";

        // 匹配文章中所有的图片标签
        Matcher matcher = Pattern.compile(regex).matcher(mdFileContent);

        // imageNames 用于存储匹配到的图片标签
        List<String> imageNames = new ArrayList<>();

        //遍历匹配项，将其添加至集合中
        while (matcher.find()) {

            // 得到当前图片标签
            String curImageLabel = matcher.group();

            // 放心大胆地使用"/"截取子串，因为文件名不能包含"/"字符
            Integer picNameStartIndex = curImageLabel.lastIndexOf("/") + 1;
            Integer picNameEndIndex = curImageLabel.length() - 1;
            // 得到图片名称
            String curImageName = curImageLabel.substring(picNameStartIndex, picNameEndIndex);

            // 添加至集合中
            imageNames.add(curImageName);

        }

        // 转换为数组返回
        String[] retStrs = new String[imageNames.size()];
        return imageNames.toArray(retStrs);
    }

    /**
     * 清除无用图片
     *
     * @param allPicFiles  本地所有的图片 File 对象
     * @param usedPicNames md 文件中使用到的图片名称
     */
    private static void CleanUnusedPic(File[] allPicFiles, String[] usedPicNames) {

        // assets文件夹中如果没有图片，则直接返回
        if (allPicFiles == null || allPicFiles.length == 0) {
            return;
        }

        // 临时文件夹，保存被删除的图片
        File deletePicDir = new File(allPicFiles[0].getParentFile().getParent() + "\\" + "deletePic");
        if (deletePicDir.exists() == false) {
            deletePicDir.mkdir();
        }

        // 获取asset文件夹的绝对路径
        String assetPath = allPicFiles[0].getParent();

        // 为了便于操作，将数组转换为List
        List<String> usedPicNameList = Arrays.asList(usedPicNames);

        // 遍历所有本地图片，看看有哪些图片没有被使用
        for (File curPicFile : allPicFiles) {

            // 如果没有被使用，则添加至unusedPicNames集合
            String curFileName = curPicFile.getName();
            boolean isUsed = usedPicNameList.contains(curFileName);
            if (!isUsed) {
                // 创建File对象，用于删除
                String curPicAbsolutePath = curPicFile.getAbsolutePath();

                // 删除文件，看看回收站还有没有，并没有。。。
                // curPicFile.delete();

                // 将待删除的图片移动至 deletePic 文件夹
                String destPicPath = deletePicDir.getAbsolutePath() + "\\" + curPicFile.getName();
                curPicFile.renameTo(new File(destPicPath));

                // 测试用：打印输出
                System.out.println("已移动无用图片至:" + destPicPath);
            }

        }

    }
}

