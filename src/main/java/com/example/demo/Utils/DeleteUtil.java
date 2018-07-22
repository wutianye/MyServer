package com.example.demo.Utils;


import com.example.demo.Entity.*;
import com.example.demo.Service.*;
import com.example.demo.Service.Impl.*;

import java.util.List;
import java.util.Set;

public class DeleteUtil {

    private static UserService userService = SpringBeanFactoryUtil.getBean(UserServiceImpl.class);
    private static UserTokenService userTokenService = SpringBeanFactoryUtil.getBean(UserTokenServiceImpl.class);
    private static UserDeviceService userDeviceService = SpringBeanFactoryUtil.getBean(UserDeviceServiceImpl.class);
    private static DeviceSensorService deviceSensorService = SpringBeanFactoryUtil.getBean(DeviceSensorServiceImpl.class);
    private static DeviceRelayService deviceRelayService = SpringBeanFactoryUtil.getBean(DeviceRelayServiceImpl.class);
    private static DataService dataService = SpringBeanFactoryUtil.getBean(DataServiceImpl.class);
    private static RedisService redisService = SpringBeanFactoryUtil.getBean(RedisServiceImpl.class);

    //删除给定devEUI、typeid有关的数据
    public static TMessage deleteData(String devEUI, String typeid) {
        dataService.deleteBydevEUIAndTypeid(devEUI, typeid);
        /*redis中的数据也要删除*/
        String pattern = "*_" + devEUI + "_" + typeid;
        Set<String> stringSet = redisService.getKeysByPattern(pattern);
        for (String key : stringSet) {
            redisService.deleteByKey(key);
        }

        List<Data> dataList = dataService.findBydevEUIAndTypeid(devEUI, typeid);
        if (dataList == null || dataList.size() == 0) {
            return new TMessage(TMessage.CODE_SUCCESS, "删除数据成功");
        }
        return new TMessage(TMessage.CODE_FAILURE, "删除数据失败");
    }

    //删除给定devEUI有关的所有数据
    public static TMessage deleteData(String devEUI) {
        dataService.deleteBydevEUI(devEUI);
        /*redis中的数据也要删除*/
        String pattern = "*_" + devEUI + "_*";
        Set<String> stringSet = redisService.getKeysByPattern(pattern);
        for (String key : stringSet) {
            redisService.deleteByKey(key);
        }

        List<Data> dataList = dataService.findBydevEUI(devEUI);
        if (dataList == null || dataList.size() == 0) {
            return new TMessage(TMessage.CODE_SUCCESS, "删除数据成功");
        }
        return new TMessage(TMessage.CODE_FAILURE, "删除数据失败");
    }

    //用户或管理员删除指定devEUI下指定的传感器
    public static TMessage deleteSensor(String devEUI, String typeid) {
        //关闭对应传感器，下发配置
        DeviceSensor deviceSensor = deviceSensorService.findBydevEUIAndTypeid(devEUI, typeid);
        if (deviceSensor.getState().equals("1")) {
            deviceSensor.setState("0");
            Info info = DeviceSensorUtil.updatestate(deviceSensorService, deviceSensor);
            if (!info.isResult()) {
                return new TMessage(TMessage.CODE_FAILURE, "关闭传感器失败！无法删除！");
            }
        }
        //关闭传感器成功，删除Data中对应的数据
        TMessage tMessage = deleteData(devEUI, typeid);
        if (tMessage.getCode() == TMessage.CODE_FAILURE) {
            return new TMessage(TMessage.CODE_FAILURE, "删除对应传感器数据失败");
        }

        //删除传感器
        deviceSensorService.deleteBydevEUIAndTypeid(devEUI, typeid);
        if (deviceSensorService.exists(devEUI, typeid)) {
            return new TMessage(TMessage.CODE_FAILURE, "删除传感器失败！");
        }
        return new TMessage(TMessage.CODE_SUCCESS, "删除传感器成功！");

    }

    //用户或管理员删除指定devEUI下所有的传感器
    public static TMessage deleteSensor(String devEUI) {
        //1.关闭所有传感器
        String str = Instructions.DOWNLINK_SENSOR_CONFIGURE + Instructions.CONFIGURE_CLOSEALL;
        String instruction = MQTTUtil.makeInstructions(str);
        if (instruction == null) {
            return new TMessage(TMessage.CODE_FAILURE, "构造关闭所有传感器配置指令失败！");
        }
        String topic = MQTTUtil.makeTopic(devEUI, "tx");
        Info info = MQTTUtil.publish(topic, MQTTUtil.makeData(instruction), "ack");
        if (!info.isResult()) {
            return new TMessage(TMessage.CODE_FAILURE, info.getInfo());
        }
        //2.删除对应的Data表中的数据
        TMessage tMessage = deleteData(devEUI);
        if (tMessage.getCode() == TMessage.CODE_FAILURE) {
            return tMessage;
        }
        //3.删除传感器
        deviceSensorService.deleteBydevEUI(devEUI);
        List<DeviceSensor> deviceSensorList = deviceSensorService.findBydevEUI(devEUI);
        if (deviceSensorList == null || deviceSensorList.size() == 0) {
            return new TMessage(TMessage.CODE_SUCCESS, "删除传感器成功！");
        }
        return new TMessage(TMessage.CODE_FAILURE, "删除传感器失败！");
    }

    //用户或管理员删除指定devEUI下指定的继电器
    public static TMessage deleteRelay(String devEUI, String relaytype) {
        deviceRelayService.deleteBydevEUIAndrelayType(devEUI, relaytype);
        if (deviceRelayService.exists(devEUI, relaytype)) {
            return new TMessage(TMessage.CODE_FAILURE, "删除继电器失败！");
        }
        return new TMessage(TMessage.CODE_SUCCESS, "删除继电器成功！");
    }

    //用户或管理员删除指定devEUI下所有继电器
    public static TMessage deleteRelay(String devEUI) {
        deviceRelayService.deleteBydevEUI(devEUI);
        List<DeviceRelay> deviceRelayList = deviceRelayService.findBydevEUI(devEUI);
        if (deviceRelayList == null || deviceRelayList.size() == 0) {
            return new TMessage(TMessage.CODE_SUCCESS, "删除继电器成功！");
        }
        return new TMessage(TMessage.CODE_FAILURE, "删除继电器失败！");
    }

    //用户或管理员删除指定设备
    public static TMessage deleteDevice(String devEUI) {
        //首先删除设备下所有传感器
       TMessage tMessage = deleteSensor(devEUI);
       if (tMessage.getCode() == TMessage.CODE_FAILURE) {
           return tMessage;
       }
       //然后删除设备下所有继电器
        tMessage = deleteRelay(devEUI);
        if (tMessage.getCode() == TMessage.CODE_FAILURE) {
            return tMessage;
        }
        //删除LoraServer上的device记录
        if (!HttpInfo.deleteDevice(devEUI)) {
            return new TMessage(TMessage.CODE_FAILURE, "删除LoRaServer上的设备记录失败！");
        }
        //删除UserDevice表中的数据
        userDeviceService.deleteBydevEUI(devEUI);
        if (userDeviceService.exists(devEUI)) {
            return new TMessage(TMessage.CODE_FAILURE, "删除设备失败！");
        }
        return new TMessage(TMessage.CODE_SUCCESS, "删除设备成功！");

    }

    //管理员根据userid删除用户
    public static TMessage deleteUser(String userid) {
        //删除用户下所有设备
        List<UserDevice> userDeviceList = userDeviceService.findAllByUserid(userid);
        TMessage tMessage;
        for (UserDevice userDevice : userDeviceList) {
            tMessage = deleteDevice(userDevice.getDevEUI());
            if (tMessage.getCode() == TMessage.CODE_FAILURE) {
                return new TMessage(TMessage.CODE_FAILURE, "执行删除设备 " + userDevice.getDevEUI() +" 错误：" + tMessage.getInfo());
            }
        }
        //删除用户token记录
        userTokenService.deleteByUserid(userid);
        if (userTokenService.exists(userid)) {
            return new TMessage(TMessage.CODE_FAILURE, "删除用户token记录失败！");
        }
        //删除用户表中信息
        userService.deleteByUserid(userid);
        if (userService.exists(userid)) {
            return new TMessage(TMessage.CODE_FAILURE, "删除用户记录失败！");
        }
        return new TMessage(TMessage.CODE_SUCCESS, "删除用户记录成功！");
    }

}
