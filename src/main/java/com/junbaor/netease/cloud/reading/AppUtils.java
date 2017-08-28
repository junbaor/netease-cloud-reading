package com.junbaor.netease.cloud.reading;

import java.util.regex.Pattern;

public class AppUtils {
    static Pattern pattern = Pattern.compile("^十.");

    public static String magic(String ss) {
        String[] split = ss.split("：");

        String title = split[1];
        String number = split[0];

        String resut = getArab(number.replaceAll("(第|章)", ""));

        resut = "第 " + resut + " 章" + "：" + title;
        System.out.println("* [" + resut + "](" + resut + ".md)");
        return resut;
    }

    private static String getArab(String ss) {
        String resut = "";

        if (ss.equals("十")) {
            resut = "10";
        } else if (pattern.matcher(ss).find()) {
            resut = coverntTen(ss);
        } else {
            resut = getString(ss);
        }
        return resut;
    }

    private static String getString(String ss) {
        String intern = ss.intern().replaceAll("(十|百|千|万)", "");
        String resut = covernt(intern);
        char lastChar = ss.charAt(ss.length() - 1);
        switch (lastChar) {
            case '十':
                resut += '0';
                break;
            case '百':
                resut += "00";
                break;
            case '千':
                resut += "000";
                break;
            case '万':
                resut += "0000";
                break;
        }
        return resut;
    }

    private static String covernt(String intern) {
        return intern.replaceAll("零", "0")
                .replaceAll("一", "1")
                .replaceAll("二", "2")
                .replaceAll("三", "3")
                .replaceAll("四", "4")
                .replaceAll("五", "5")
                .replaceAll("六", "6")
                .replaceAll("七", "7")
                .replaceAll("八", "8")
                .replaceAll("九", "9");
    }

    private static String coverntTen(String intern) {
        return covernt(intern).replaceAll("十", "1");
    }


}
