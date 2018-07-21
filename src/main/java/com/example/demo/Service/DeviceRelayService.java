package com.example.demo.Service;

import com.example.demo.Entity.DeviceRelay;

import java.util.List;

public interface DeviceRelayService {
    //插入一条数据/更新数据（更新继电器状态）
    void insert(DeviceRelay deviceRelay);

    //根据devEUI和typeid判断是否存在
    boolean exists(String devEUI, String relayType);

    //根据devEUI查询所有的relaytype
    List<DeviceRelay> findBydevEUI(String devEUI);

    //根据devEUI删除数据
    void deleteBydevEUI(String devEUI);

    //根据devEUI、relayType删除一条数据
    void deleteBydevEUIAndrelayType(String devEUI, String relayType);

}
