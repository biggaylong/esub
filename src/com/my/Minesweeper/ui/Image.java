package com.my.Minesweeper.ui;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Image {
    // 图片缓存，避免重复加载
    private static Map<String, Icon> imageCache = new HashMap<>();

    // 图片资源路径
    private static final String RESOURCE_PATH = "/images/";

    // 图片文件名常量
    public static final String EMPTY = "empty.png";
    public static final String MINE = "mine.png";
    public static final String MARKED = "marked.png";
    public static final String CLOSED = "closed.png";
    public static final String NUMBER_1 = "number_1.png";
    public static final String NUMBER_2 = "number_2.png";
    public static final String NUMBER_3 = "number_3.png";
    public static final String NUMBER_4 = "number_4.png";
    public static final String NUMBER_5 = "number_5.png";
    public static final String NUMBER_6 = "number_6.png";
    public static final String NUMBER_7 = "number_7.png";
    public static final String NUMBER_8 = "number_8.png";
    public static final String EXPLODED = "exploded.png";

    // 加载图片资源
    public static Icon getImage(String imageName) {
        if (imageCache.containsKey(imageName)) {
            return imageCache.get(imageName);
        }

        try {
            // 使用类加载器加载图片资源，支持打包在JAR文件中
            java.net.URL imgURL = Image.class.getResource(RESOURCE_PATH + imageName);
            if (imgURL != null) {
                // 调整图片大小为30x30像素
                Icon icon = new ImageIcon(imgURL);
                Icon scaledIcon = new ImageIcon(((ImageIcon) icon).getImage().getScaledInstance(
                        44, 44, java.awt.Image.SCALE_SMOOTH));
                imageCache.put(imageName, scaledIcon);
                return scaledIcon;
            } else {
                System.err.println("Couldn't find file: " + RESOURCE_PATH + imageName);
                // 如果找不到图片，返回一个空图标
                return new ImageIcon(new java.awt.image.BufferedImage(30, 30, java.awt.image.BufferedImage.TYPE_INT_ARGB));
            }
        } catch (Exception e) {
            System.err.println("Error loading image: " + imageName);
            e.printStackTrace();
            // 出错时返回空图标
            return new ImageIcon(new java.awt.image.BufferedImage(30, 30, java.awt.image.BufferedImage.TYPE_INT_ARGB));
        }
    }

    // 获取数字对应的图片
    public static Icon getNumberImage(int number) {
        switch (number) {
            case 1: return getImage(NUMBER_1);
            case 2: return getImage(NUMBER_2);
            case 3: return getImage(NUMBER_3);
            case 4: return getImage(NUMBER_4);
            case 5: return getImage(NUMBER_5);
            case 6: return getImage(NUMBER_6);
            case 7: return getImage(NUMBER_7);
            case 8: return getImage(NUMBER_8);
            default: return getImage(EMPTY);
        }
    }
}
