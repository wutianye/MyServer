package com.example.demo.Utils;

import com.example.demo.Entity.UserDevice;
import com.example.demo.Service.Impl.UserDeviceServiceImpl;
import com.example.demo.Service.UserDeviceService;

import java.util.HashMap;
import java.util.List;

public class RealTimeUtil {

    private static UserDeviceService userDeviceService = SpringBeanFactoryUtil.getBean(UserDeviceServiceImpl.class);

    public static HashMap<String, Boolean> userState = new HashMap<String, Boolean>();

    //根据websocket接入的用户去订阅消息
    public static void subscribeByUserid(String userid) {
        List<UserDevice> userDeviceList = userDeviceService.findAllByUserid(userid);
        changeUserState(userid, true);
        for (UserDevice userDevice : userDeviceList) {
            String topic = MQTTUtil.makeTopic(userDevice.getDevEUI(), "rx");
            MQTTUtil.subscribe(topic, userid);
        }
    }

    //用户关闭webSocket连接，取消之前的所有订阅
    public static void cancleSubscribe(String userid) {
        changeUserState(userid, false);
    }

    //webSocket在线用户状态
    public static void changeUserState(String userid, Boolean state){
        userState.put(userid, state);
//        if (!userState.get(userid)) {
//            userState.remove(userid);
//        }
    }

}
