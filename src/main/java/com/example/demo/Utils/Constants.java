package com.example.demo.Utils;

public class Constants {


    //MQTT部分常量
    final public static String MQTT_HOST = "tcp://39.106.54.222:1883";
    final public static String MQTT_USERNAME = "cdx";
    final public static String MQTT_PASSWORD = "cdxhhhhh";
    final public static String MQTT_TOPIC = "application/2/device/004a770066003304/rx";
    final public static String MQTT_TOPIC_PREFIX = "application/2/device/";
//    final public static String MQTT_HOST = "tcp://www.liuyunxing.cn:1883/";
//    final public static String MQTT_USERNAME = "test2";
//    final public static String MQTT_PASSWORD = "123456";
//    final public static String MQTT_TOPIC = "application/5/device/+/rx";
//    final public static String MQTT_TOPIC_PREFIX = "application/5/device/";


    //LoRaServer有关接口 Http有关常量
    final public static String HTTP_REQUESTURL_PREFIX = "https://www.liuyunxing.cn:8080";
    final public static String HTTP_LORASERVER_USERNAME = "test2";
    final public static String HTTP_LORASERVER_PASSWORD = "123456";

    //添加设备时的默认配置
    final public static String ADD_DEVICE_DEFAULT_DESCRIPTION = "这是描述";
    final public static String ADD_DEVICE_DEFAULT_DEVICEPROFILEID = "0391774c-8fcb-46b1-a225-9faef7d57fe1";

    //默认上传速率（在添加设备时设置的默认值）
    final public static int DEFAULT_FREQUENCY = 8;


}
