package com.heygo.typora.util.typora;

import java.io.*;

/**
 * @ClassName TyporaFileRwUtil
 * @Description TODO
 * @Author Heygo
 * @Date 2020/7/24 12:55
 * @Version 1.0
 */
public class TyporaFileRwUtil {

    /**
     * 读取MD文件的内容
     *
     * @param curFile MD文件的File对象
     * @return MD文件的内容
     */
    /** 
     * @description:
     * @param: curFile
     * @return: java.lang.String
     * @author Heygo
     * @date: 15:18 2020/12/23
     */ 
    public static String readMdFileContent(File curFile) {

        // 存储md文件内容
        StringBuilder sb = new StringBuilder();

        // 当前行内容
        String curLine;

        // 装饰者模式：FileReader无法一行一行读取，所以使用BufferedReader装饰FileReader
        try (
                FileReader fr = new FileReader(curFile);
                BufferedReader br = new BufferedReader(fr);
        ) {
            // 当前行有内容
            while ((curLine = br.readLine()) != null) {
                sb.append(curLine + "\r\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 返回md文件内容
        return sb.toString();
    }


    /**
     * 保存MD文件
     *
     * @param destMdFilePath MD文件路径
     * @param mdFileContent  MD文件内容
     */
    public static void SaveMdContentToFile(String destMdFilePath, String mdFileContent) {

        // 不保存空文件
        if (mdFileContent == null || mdFileContent == "") {
            return;
        }

        // 执行保存
        try (FileWriter fw = new FileWriter(destMdFilePath)) {
            fw.write(mdFileContent);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
