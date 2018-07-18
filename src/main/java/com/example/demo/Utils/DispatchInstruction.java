package com.example.demo.Utils;

public class DispatchInstruction {

    /**
     * 作用：打开温湿度氨气三合一、风速传感器
     * 16进制指令：ff 02 02 03 00 20 00 03 ff 01 01 03 00 2a 00 01 ff ad 60
     */
    final public static String OPEN_01_02 = "/wICAwAgAAP/AQEDACoAAf+tYA==";

    /**
     * 作用：仅打开温湿度氨气三合一传感器
     * 16进制指令：ff 02 02 03 00 20 00 03 ff a6 5e
     */
    final public static String OPEN_02 = "/wICAwAgAAP/pl4=";

    /**
     * 作用：仅打开风速传感器
     * 16进制指令：ff 01 01 03 00 2a 00 01 ff d7 f3
     */
    final public static String OPEN_01 = "/wEBAwAqAAH/1/M=";

    /**
     * 作用：关闭所有传感器
     * 16进制指令：ff ff 00 00
     */
    final public static String CLOSE_ALL = "//8AAA==";


    /**
     * 作用：
     * 16进制指令：
     */


}
