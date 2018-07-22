package com.example.demo.Utils;

import org.fusesource.mqtt.client.*;

import java.net.URISyntaxException;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

public class MQTTUtil {

    private static final Base64.Encoder encoder = Base64.getEncoder();
    //订阅
    public static Info subscribe(String topic) {
        Info info = new Info();

        MQTT mqtt = new MQTT();
        try {
            mqtt.setHost(Constants.MQTT_HOST);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        mqtt.setUserName(Constants.MQTT_USERNAME);
        mqtt.setPassword(Constants.MQTT_PASSWORD);
        BlockingConnection connection = mqtt.blockingConnection();
        try {
            connection.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("mqtt连接成功！订阅" + topic +"：");

        Topic[] topics = {new Topic(topic, QoS.AT_LEAST_ONCE)};
        try {
            byte[] qoses = connection.subscribe(topics);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Message message = null;
        try {
            message = connection.receive();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(message.getTopic() + new String(message.getPayload()));
        //处理数据

        message.ack();
        try {
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }


    //发布  处理ack、继电器状态,type对应为ack，rstate
    public static Info publish(String topic, String data, String type) {
        Info info = new Info();
        if (!(type.equals("ack") || type.equals("rstate"))) {
            info.setResult(false);
            info.setInfo("无效的选项！");
            return info;
        }
        MQTT mqtt = new MQTT();
        try {
            mqtt.setHost(Constants.MQTT_HOST);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        mqtt.setUserName(Constants.MQTT_USERNAME);
        mqtt.setPassword(Constants.MQTT_PASSWORD);
        BlockingConnection connection = mqtt.blockingConnection();
        try {
            connection.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String topic2 = topic.substring(0, topic.length() - 2) + "rx";
        Topic[] topics = {new Topic(topic2, QoS.AT_LEAST_ONCE)};
        System.out.println("mqtt连接成功！发布" + topic +"：");
        System.out.println("订阅" + topic2 +"：");
        try {
            connection.publish(topic, data.getBytes(), QoS.AT_LEAST_ONCE, false);
            connection.subscribe(topics);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //发布之后要根据返回的ACK判断该操作是否成功，并将结果告知前端，或者将获取的继电器状态结果返回
        Message message = null;
        try {
            message = connection.receive(15, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (message == null) {
            System.out.println("timeout!!");
            info.setResult(false);
            info.setInfo("timeout!");
            return info;
        }
        System.out.println(message.getTopic() + new String(message.getPayload()));
        //处理数据
        switch (type) {
            case "ack":
                info.setResult(DataProcess.ackHander(new String(message.getPayload())));
                if (info.isResult()) {
                    info.setInfo("指令执行成功！");
                } else {
                    info.setInfo("ack处理异常！");
                }
                break;
            case "rstate":
                String result = DataProcess.rstateHander(new String(message.getPayload()));
                if (result == null) {
                    info.setResult(false);
                    info.setInfo("rstate处理异常！");
                } else {
                    info.setResult(true);
                    info.setInfo(result);
                }
                break;
            default:
                info.setResult(false);
                info.setInfo("未知错误！");
        }
        message.ack();
        try {
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }


    //构造发布data结构
    public static String makeData(String data) {
        //base64编码
        byte[] bytes = CRC16Modbus.HexString2Bytes(data);
        String base64 = encoder.encodeToString(bytes);
        System.out.println("base64encoder:" + base64);
        return "{\"reference\": \"aaaa\", \"fPort\": 10, \"confirm\": false, \"data\": \""
                + base64 + "\"}";
    }

    //构造topic
    //type : rx   tx
    public static String makeTopic(String devEUI, String type) {
        return Constants.MQTT_TOPIC_PREFIX + devEUI + "/" + type;
    }

    //构造指令（构造传感器开关指令）
    public static String makeInstructions(String type, String typeid, String state) {
        String str = type + typeid + state + "ff";
        System.out.println("str:" + str);
        String strCRC = CRC16Modbus.makeCRC(str);
        if (strCRC != null) {
            return str + strCRC;
        }
        return null;
    }
    //构造指令
    public static String makeInstructions(String str) {
        String strCRC = CRC16Modbus.makeCRC(str);
        if (strCRC != null) {
            return str + strCRC;
        }
        return null;
    }

}
