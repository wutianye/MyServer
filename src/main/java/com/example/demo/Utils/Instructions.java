package com.example.demo.Utils;

public class Instructions {

    //传感器编号
    final public static String SENSOR_WIND = "01";//风速传感器
    final public static String SENSOR_GTH = "02";//温湿度氨气三合一
    final public static String SENSOR_GPS = "03";//GPS传感器


    //传感器开关指令
    final public static String SENSOR_OPEN = "01";//传感器开
    final public static String SENSOR_CLOSE = "00";//传感器关

    //继电器编号
    final public static String RELAY_01 = "01";//8路继电器

    //继电器状态查询指令码
    final public static String RELAY_STATE_GET = "00";

    //继电器下各开关的状态码
    final public static String RELAY_SWITCH_OPEN = "01";
    final public static String RELAY_SWITCH_CLOSE = "00";


    //下发前置字段
    final public static String DOWNLINK_SENSOR_CONFIGURE = "f1";//下发传感器配置
    final public static String DOWNLINK_SENSOR_SWITCH = "f2";//下发传感器开关指令
    final public static String DOWNLINK_RELAY_CONTROL = "f3";//下发继电器控制指令


    //上传前置字段
    final public static String UPLINK_ACK = "e1";//上传回应的ACK
    final public static String UPLINK_SENSOR_DATA = "e2";//上传传感器数据
    final public static String UPLINK_RELAY_STATE = "e3";//上传继电器状态
    final public static String UPLINK_CONFIGURE = "e4";//上传指令，请求配置


    //配置指令（不含CRC）
    //关闭所有传感器（需要加上下发配置头）
    final public static String CONFIGURE_CLOSEALL = "ff";
    //仅打开风速传感器（中间一串是485命令）
    final public static String CONFIGURE_OPEN_WIND = SENSOR_WIND + "0103002a0001" + "ff";
    //仅打开温湿度氨气三合一传感器
    final public static String CONFIGURE_OPEN_GTH = SENSOR_GTH + "020300200003" + "ff";
    //打开温湿度氨气三合一和风速传感器，可通过上述两个指令拼接


}
