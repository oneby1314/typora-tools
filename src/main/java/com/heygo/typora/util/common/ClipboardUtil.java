package com.heygo.typora.util.common;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

/**
 * @ClassName ClipboardUtil
 * @Description TODO
 * @Author Oneby
 * @Date 2021/2/5 10:39
 * @Version 1.0
 */
public class ClipboardUtil {

    /***
     * @description: 将文本的复制到系统的粘贴板中
     * @param: text     复制到粘贴板中的文本
     * @return: void
     * @author Oneby
     * @date: 10:42 2021/2/5
     */
    public static void setClipboardString(String text) {
        // 获取系统剪贴板
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        // 封装文本内容
        Transferable trans = new StringSelection(text);
        // 把文本内容设置到系统剪贴板
        clipboard.setContents(trans, null);
    }

    /***
     * @description: 获取系统粘贴板的内容
     * @param:
     * @return: java.lang.String 粘贴板中的内容
     * @author Oneby
     * @date: 10:42 2021/2/5
     */
    public static String getClipboardString() {
        // 获取系统剪贴板
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        // 获取剪贴板中的内容
        Transferable trans = clipboard.getContents(null);

        if (trans != null) {
            // 判断剪贴板中的内容是否支持文本
            if (trans.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                try {
                    // 获取剪贴板中的文本内容
                    String text = (String) trans.getTransferData(DataFlavor.stringFlavor);
                    return text;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

}
