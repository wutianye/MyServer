package com.example.demo.Utils;

import org.fusesource.mqtt.client.*;

import java.net.URISyntaxException;

public class MQTTUtil {

    //订阅
    public static void subscribe(String topic, String userid) {
        MQTT mqtt = new MQTT();
        try {
            mqtt.setHost("tcp://39.106.54.222:1883");
//			mqtt.setHost("tcp://www.liuyunxing.cn:1883/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
//		mqtt.setUserName("test2");
//		mqtt.setPassword("123456");
        mqtt.setUserName("cdx");
        mqtt.setPassword("cdxhhhhh");
        BlockingConnection connection = mqtt.blockingConnection();
        try {
            connection.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("mqtt连接成功！订阅" + topic +"：");
        //退出条件根据webSocket userid的状态实现，设置一个状态值
        while(RealTimeUtil.userState.get(userid)) {
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
            String data = message.getTopic() + new String(message.getPayload());

//        DataProcess.getDataFromTopicAndPayLoad(message.getTopic(), new String(message.getPayload()));
            message.ack();
        }
        System.out.println("=======退出！======");
        try {
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //发布
    public static void publish(String topic, String data) {
        MQTT mqtt = new MQTT();
        try {
            mqtt.setHost("tcp://39.106.54.222:1883");
//			mqtt.setHost("tcp://www.liuyunxing.cn:1883/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
//		mqtt.setUserName("test2");
//		mqtt.setPassword("123456");
        mqtt.setUserName("cdx");
        mqtt.setPassword("cdxhhhhh");
        BlockingConnection connection = mqtt.blockingConnection();
        try {
            connection.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("mqtt连接成功！发布" + topic +"：");

        Topic[] topics = {new Topic(topic, QoS.AT_LEAST_ONCE)};
        try {
            connection.publish(topic, data.getBytes(), QoS.AT_LEAST_ONCE, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //发布之后要根据返回的ACK判断该操作是否成功，并将结果告知前端

        System.out.println("=======退出！======");
        try {
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

//        }
    }


    //构造发布data结构
    public static String makeData(String data) {
        return "{\"reference\": \"aaaa\", \"fPort\": 10, \"confirm\": false, \"data\": \""
                + data + "\"}";
    }

    //构造topic
    //type : rx   tx
    public static String makeTopic(String devEUI, String type) {
        return "application/2/device/" + devEUI + "/" + type;
    }

}
