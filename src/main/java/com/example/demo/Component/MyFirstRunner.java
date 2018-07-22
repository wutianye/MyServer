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


    }

}
