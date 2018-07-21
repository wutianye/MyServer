package com.example.demo.Service;

import com.example.demo.Entity.DeviceSensor;

import java.util.List;

public interface DeviceSensorService {

    //插入一条数据/更新数据（更新传感器状态）
    void insert(DeviceSensor deviceSensor);

    //根据devEUI和typeid判断是否存在
    boolean exists(String devEUI, String typeid);

    //根据devEUI查询所有的typeid
    List<DeviceSensor> findBydevEUI(String devEUI);

    //根据devEUI、typeid获取对应的一条数据
    DeviceSensor findBydevEUIAndTypeid(String devEUI, String typeid);

    //根据devEUI删除数据
    void deleteBydevEUI(String devEUI);

    //根据devEUI、typeid删除一条数据
    void deleteBydevEUIAndTypeid(String devEUI, String typeid);

}
