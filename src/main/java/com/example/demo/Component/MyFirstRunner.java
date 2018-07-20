package com.example.demo.Component;

import com.example.demo.Utils.DataProcess;
import com.example.demo.Utils.HttpInfo;
import org.fusesource.mqtt.client.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;

@Component
@Order(1)
public class MyFirstRunner implements CommandLineRunner{

    /**
     *登陆LoRaServer
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
        /**
         * 登陆LoRaServer
         */
        String state = HttpInfo.login();
        System.out.println(state);


        /**
         * 开一个线程去监听join的请求
         */
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                MQTT mqtt = new MQTT();
//                try {
//                    mqtt.setHost("tcp://39.106.54.222:1883");
////			mqtt.setHost("tcp://www.liuyunxing.cn:1883/");
//                } catch (URISyntaxException e) {
//                    e.printStackTrace();
//                }
////		mqtt.setUserName("test2");
////		mqtt.setPassword("123456");
//                mqtt.setUserName("cdx");
//                mqtt.setPassword("cdxhhhhh");
//                BlockingConnection connection = mqtt.blockingConnection();
//                try {
//                    connection.connect();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                //			Topic[] topics = {new Topic("application/#", QoS.AT_LEAST_ONCE)};
//                Topic[] topics = {new Topic("application/2/device/+/join", QoS.AT_LEAST_ONCE)};
////            Topic[] topics = {new Topic("application/5/device/+/rx", QoS.AT_LEAST_ONCE)};
//                try {
//                    byte[] qoses = connection.subscribe(topics);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                System.out.println("mqtt连接成功！订阅application/2/device/+/join：");
//                while(true) {
//                    Message message = null;
//                    try {
//                        message = connection.receive();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    System.out.println("join:" + message.getTopic() + new String(message.getPayload()));
//                    //byte[] payload = message.getPayload();
//                    // process the message then:
//                    DataProcess.joinHander(message.getTopic(), new String(message.getPayload()));
//                    message.ack();
//                }
//            }
//        }).start();

    }

}
