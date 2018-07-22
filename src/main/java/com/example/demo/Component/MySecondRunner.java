package com.example.demo.Component;


import com.example.demo.Utils.Constants;
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
        Topic[] topics = {new Topic(Constants.MQTT_TOPIC, QoS.AT_LEAST_ONCE)};

        try {
            byte[] qoses = connection.subscribe(topics);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("mqtt连接成功！订阅" + Constants.MQTT_TOPIC + ":");
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
