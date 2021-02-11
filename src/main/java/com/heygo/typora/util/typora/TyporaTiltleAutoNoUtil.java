package com.heygo.typora.util.typora;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

/**
 * @ClassName TyporaTiltleAutoNoUtil
 * @Description TODO
 * @Author Heygo
 * @Date 2020/7/24 12:43
 * @Version 1.0
 */
public class TyporaTiltleAutoNoUtil {

    /**
     * 执行单个文件的标题自动编号
     *
     * @param mdFileContent md 文件的内容
     * @return 标题编号之后的 md 文件内容
     */
    public static String doSingleMdTitleAutoNo(String mdFileContent) {
        return getAutoTitledMdContent(mdFileContent);
    }

    /**
     * 执行单个文件的标题自动编号
     *
     * @param mdFileContent md 文件的内容
     * @return 标题编号之后的 md 文件内容
     */
    private static String getAutoTitledMdContent(String mdFileContent) {
        // 标题编号
        /*
        标题编号规则：
            - 一级标题为文章的题目，不对一级标题编号
            - 二级、三级、四级标题需要级联编号
            - 五级、六级标题无需级联编号，只需看上一级标题的脸色，递增即可
         */
        Integer[] titleNumber = new Integer[]{0, 0, 0, 0, 0};

        // 存储md文件内容
        StringBuilder sb = new StringBuilder();

        // 当前行内容
        String curLine;

        // 当前内容是否在代码块中
        boolean isCodeBLock = false;
        int spaceCount = 0;

        try (
                StringReader sr = new StringReader(mdFileContent);
                BufferedReader br = new BufferedReader(sr);
        ) {
            while ((curLine = br.readLine()) != null) {

                // 忽略代码块
                if (curLine.trim().startsWith("```")) {

                    // 这里处理代码块的逻辑有 Bug ，很奇怪，Debug 明明显示我的 Markdown 文本是对的。。。

                    // 如果已经在代码块内，该判断这次的 ``` 是否需要结束此代码块
                    if (isCodeBLock == true) {
                        if (spaceCount == (curLine.length() - (curLine + "S").trim().length() + 1)) {
                            isCodeBLock = !isCodeBLock;
                            spaceCount = 0;
                        }
                    } else {
                        // 否则为新代码块的开始，记录 ``` 前面有几个空格
                        isCodeBLock = !isCodeBLock;
                        spaceCount = curLine.length() - (curLine + "S").trim().length() + 1;
                    }
                }

                if (isCodeBLock == false) {
                    // 判断是否为标题行，如果是标题，是几级标题
                    Integer curTitleLevel = calcTitleLevel(curLine);

                    if (curTitleLevel != -1) {

                        // 插入标题序号
                        curLine = insertTitleNumber(curLine, titleNumber);

                        // 重新计算标题计数器
                        RecalcTitleCounter(curTitleLevel, titleNumber);

                    }
                }

                // 向缓冲区中追加内容
                sb.append(curLine + "\r\n");
            }

            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 根据当前行的内容，计算标题的等级
     *
     * @param curLine 当前行的内容
     * @return 当前行的标题等级：-1 表示不是标题行，>=2 的正数表示标题的等级
     */
    private static Integer calcTitleLevel(String curLine) {

        // 由于一级标题无需编号，所以从二级标题开始判断
        boolean isTitle = curLine.startsWith("##");
        if (!isTitle) {

            // 返回 -1 表示非标题行
            return -1;
        }

        // 现在来看看是几级标题
        Integer titleLevel = curLine.indexOf(" ");

        return titleLevel;

    }

    /**
     * 在当前行前面插入标题的等级
     *
     * @param curLine     当前行的内容
     * @param titleNumber 标题计数器
     * @return 添加标题之后的行
     */
    private static String insertTitleNumber(String curLine, Integer[] titleNumber) {

        // 标题等级（以空格分隔的前提是 Typora 开启严格模式）
        Integer titleLevel = curLine.indexOf(" ");

        // 标题等级部分
        String titleLevelStr = curLine.substring(0, titleLevel);

        // 标题内容部分
        String titleContent = curLine.substring(titleLevel + 1);

        // 先去除之前的编号
        titleContent = RemovePreviousTitleNumber(titleContent);

        // 标题等级递增
        Integer titleIndex = titleLevel - 2;
        if (titleIndex > 5) {
            System.out.println(titleIndex);
        }
        titleNumber[titleIndex] += 1;

        // 标题序号
        String titleNumberStr = "";
        switch (titleLevel) {
            case 2:
                titleNumberStr = titleNumber[0].toString();
                break;
            case 3:
                titleNumberStr = titleNumber[0].toString() + "." + titleNumber[1];
                break;
            case 4:
                titleNumberStr = titleNumber[0].toString() + "." + titleNumber[1] + "." + titleNumber[2];
                break;
            case 5:
                titleNumberStr = titleNumber[3].toString();
                break;
            case 6:
                titleNumberStr = titleNumber[4].toString() + " ) ";
                break;
        }
        titleNumberStr += "、";

        // 插入标题序号
        titleContent = titleNumberStr + titleContent;

        System.out.println("已增加标题序号：" + titleContent);

        // 返回带序号的标题
        curLine = titleLevelStr + " " + titleContent;
        return curLine;
    }

    /**
     * 当上一级标题更新时，需重置子级标题计数器
     *
     * @param titleLevel  当前标题等级
     * @param titleNumber 标题计数器
     */
    private static void RecalcTitleCounter(Integer titleLevel, Integer[] titleNumber) {

        // 二级标题更新时，三级及三级以下的标题序号重置为 0
        Integer startIndex = titleLevel - 1;
        for (int i = startIndex; i < titleNumber.length; i++) {
            titleNumber[i] = 0;
        }

    }

    /**
     * 移除之前的标题编号
     *
     * @param curLine 当前行内容
     * @return 移除标题编号之后的行
     */
    private static String RemovePreviousTitleNumber(String curLine) {

        // 寻找标题中的 、 字符
        Integer index = curLine.indexOf("、");

        if (index > 0) {
            // 之前已经进行过标号
            return curLine.substring(index + 1);
        } else {
            // 之前未进行过标号，直接返回
            return curLine;
        }
    }

}
