package com.example.demo.Component;


import com.example.demo.Utils.DataProcess;
import org.fusesource.mqtt.client.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;

@Component
@Order(2)
public class MySecondRunner implements CommandLineRunner{
    /**
     * 开启mqtt接收数据
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
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
        //			Topic[] topics = {new Topic("application/#", QoS.AT_LEAST_ONCE)};
        Topic[] topics = {new Topic("application/2/device/+/rx", QoS.AT_LEAST_ONCE)};
//            Topic[] topics = {new Topic("application/5/device/+/rx", QoS.AT_LEAST_ONCE)};
        try {
            byte[] qoses = connection.subscribe(topics);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("mqtt连接成功！订阅application/2/device/+/rx：");
        while(true) {
            Message message = null;
            try {
                message = connection.receive();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(message.getTopic() + new String(message.getPayload()));
            //byte[] payload = message.getPayload();
            // process the message then:
            DataProcess.getDataFromTopicAndPayLoad(message.getTopic(), new String(message.getPayload()));
            message.ack();
        }
    }
}
