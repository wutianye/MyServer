package com.example.demo.Utils;

public class Instructions {

    //传感器编号
    final public static String SENSOR_WIND = "01";//风速传感器
    final public static String SENSOR_GTH = "02";//温湿度氨气三合一


    //传感器开关指令
    final public static String SENSOR_OPEN = "01";//传感器开
    final public static String SENSOR_CLOSE = "00";//传感器关

    //继电器编号
    final public static String RELAY_01 = "01";//8路继电器


    //下发前置字段
    final public static String DOWNLINK_SENSOR_CONFIGURE = "f1";//下发传感器配置
    final public static String DOWNLINK_SENSOR_SWITCH = "f2";//下发传感器开关指令
    final public static String DOWNLINK_RELAY_CONTROL = "f3";//下发继电器控制指令


    //上传前置字段
    final public static String UPLINK_ACK = "e1";//上传回应的ACK
    final public static String UPLINK_SENSOR_DATA = "e2";//上传传感器数据
    final public static String UPLINK_RELAY_STATE = "e3";//上传继电器状态


}
