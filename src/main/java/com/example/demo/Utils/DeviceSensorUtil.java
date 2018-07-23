package com.example.demo.Utils;

import com.example.demo.Entity.DeviceSensor;
import com.example.demo.Entity.SensorType;
import com.example.demo.Service.DeviceSensorService;
import com.example.demo.Service.SensorTypeService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DeviceSensorUtil {

    //为指定的devEUI添加一个传感器
    public static Info addsensor(DeviceSensorService deviceSensorService, String devEUI, String typeid){
        Info info = new Info();
        if (deviceSensorService.exists(devEUI, typeid)) {
            info.setResult(false);
            info.setInfo("该类型传感器已存在!");
            return info;
        }
        DeviceSensor deviceSensor = new DeviceSensor(devEUI, typeid, "0");
        deviceSensorService.insert(deviceSensor);
        if (deviceSensorService.exists(devEUI, typeid)) {
            info.setResult(true);
            info.setInfo("添加成功！");
        } else {
            info.setResult(false);
            info.setInfo("添加失败！未知错误");
        }
        return info;
    }

    //获取指定devEUI下的所有传感器typeid,typename和状态
    public static List<HashMap<String,String>> getsensor(DeviceSensorService deviceSensorService, SensorTypeService sensorTypeService, String devEUI) {
        List<HashMap<String, String>> hashMapList = new ArrayList<HashMap<String, String>>();
        List<DeviceSensor> deviceSensorList = deviceSensorService.findBydevEUI(devEUI);
        for (DeviceSensor deviceSensor : deviceSensorList) {
            HashMap<String, String> hashMap = new HashMap<String, String>();
            hashMap.put("typeid", deviceSensor.getTypeid());
            SensorType sensorType = sensorTypeService.getasensortype(deviceSensor.getTypeid());
            hashMap.put("typename", sensorType.getTypename());
            hashMap.put("state", deviceSensor.getState());
            hashMapList.add(hashMap);
        }
        return hashMapList;
    }
    //获取指定devEUI下的所有传感器typeid,typename和状态
    /* *
     *
     * 功能描述: 获得指定的设备下的传感器的类别，并且返回指标列表
     *
     * @param: [deviceSensorService, sensorTypeService, devEUI]
     * @return: java.util.List<java.util.HashMap<java.lang.String,java.lang.String>>
     * @auther: liuyunxing
     * @Description //TODO
     * @date: 2018/7/15 13:52
     * 对于三合一的传感器，将数据拆分处理返回
     */
    public static List<HashMap<String,String>> getSensor(DeviceSensorService deviceSensorService, SensorTypeService sensorTypeService, String devEUI) {
        List<HashMap<String, String>> hashMapList = new ArrayList<HashMap<String, String>>();

        List<DeviceSensor> deviceSensorList = deviceSensorService.findBydevEUI(devEUI); // 获得传感器列表
        for (DeviceSensor deviceSensor : deviceSensorList) {
            if (deviceSensor.getTypeid().equals("02")){
                //三合一的传感器
                String[] choice = {"wendu","shidu", "qiti"};
                String[] name = {"temperature","humidity","gas"};
                String[] label = {"温度","湿度","气体浓度"};
                SensorType sensorType = sensorTypeService.getasensortype(deviceSensor.getTypeid());
                for (int i=0;i<3;i++){
               /*     hashMap.put("typeId",deviceSensor.getTypeid());*/
                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    hashMap.put("typeid", deviceSensor.getTypeid());
                    hashMap.put("typename", sensorType.getTypename());
                    hashMap.put("state", deviceSensor.getState());
                    hashMap.put("label",label[i]);
                    hashMap.put("choice",choice[i]);
                    hashMap.put("name",name[i]);
                    hashMapList.add(hashMap);
                }
            }else if (deviceSensor.getTypeid().equals("01")){
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("typeid", deviceSensor.getTypeid());
                SensorType sensorType = sensorTypeService.getasensortype(deviceSensor.getTypeid());
                hashMap.put("typename", sensorType.getTypename());
                hashMap.put("state", deviceSensor.getState());
                hashMap.put("label","风速");
                hashMap.put("choice","");
                hashMap.put("name","wind");
                hashMapList.add(hashMap);
            }
        }
        return hashMapList;
    }
    //更新传感器状态
    public static Info updatestate(DeviceSensorService deviceSensorService, DeviceSensor deviceSensor) {
        Info info = new Info();
        DeviceSensor deviceSensor1 = deviceSensorService.findBydevEUIAndTypeid(deviceSensor.getDevEUI(), deviceSensor.getTypeid());
        if (deviceSensor1.getState() == deviceSensor.getState()) {
            info.setResult(false);
            info.setInfo("状态未改变！");
            return info;
        }
        //构造指令
        String instruction;
        if (deviceSensor.getState().equals("1")) {
            instruction = MQTTUtil.makeInstructions(Instructions.DOWNLINK_SENSOR_SWITCH, deviceSensor.getTypeid(), Instructions.SENSOR_OPEN);
        } else if (deviceSensor.getState().equals("0")){
            instruction = MQTTUtil.makeInstructions(Instructions.DOWNLINK_SENSOR_SWITCH, deviceSensor.getTypeid(), Instructions.SENSOR_CLOSE);
        } else {
            info.setResult(false);
            info.setInfo("无效的状态！");
            return info;
        }
        if (instruction == null) {
            info.setResult(false);
            info.setInfo("创建下发指令失败！");
            return info;
        }

        //下发指令开始，处理ack
        String topic = MQTTUtil.makeTopic(deviceSensor.getDevEUI(), "tx");
        int count = 3;
        System.out.println("改变传感器状态...");
        while(count > 0){
            info = MQTTUtil.publish(topic, MQTTUtil.makeData(instruction), "ack");
            count--;
            if (info.isResult()) {
                break;
            }
        }

        if (!info.isResult()) {
            System.out.println("改变传感器状态失败！");
            return info;
        }
        System.out.println("改变传感器状态成功！");

        deviceSensor1.setState(deviceSensor.getState());
        deviceSensorService.insert(deviceSensor1);
        deviceSensor = deviceSensorService.findBydevEUIAndTypeid(deviceSensor.getDevEUI(), deviceSensor.getTypeid());
        if (deviceSensor1.getState() == deviceSensor.getState()) {
            info.setResult(true);
            info.setInfo("状态已改变！");
        } else {
            info.setResult(false);
            info.setInfo("状态修改失败！");
        }
        return info;
    }


}
