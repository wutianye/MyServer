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
        DeviceSensor deviceSensor = new DeviceSensor(devEUI, typeid, false);
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
            hashMap.put("state", String.valueOf(deviceSensor.isState()));
            hashMapList.add(hashMap);
        }
        return hashMapList;
    }

    //更新传感器状态
    public static Info updatestate(DeviceSensorService deviceSensorService, DeviceSensor deviceSensor) {
        Info info = new Info();
        DeviceSensor deviceSensor1 = deviceSensorService.findBydevEUIAndTypeid(deviceSensor.getDevEUI(), deviceSensor.getTypeid());
        if (deviceSensor1.isState() == deviceSensor.isState()) {
            info.setResult(false);
            info.setInfo("状态未改变！");
            return info;
        }
        deviceSensorService.insert(deviceSensor1);
        deviceSensor1 = deviceSensorService.findBydevEUIAndTypeid(deviceSensor.getDevEUI(), deviceSensor.getTypeid());
        if (deviceSensor1.isState() == deviceSensor.isState()) {
            info.setResult(true);
            info.setInfo("状态已改变！");
        } else {
            info.setResult(false);
            info.setInfo("状态修改失败！");
        }
        return info;
    }

}
