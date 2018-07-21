package com.example.demo.Service;

import com.example.demo.Entity.UserDevice;

import java.util.HashMap;
import java.util.List;

public interface UserDeviceService {

    //添加一个设备
    void insert(UserDevice userDevice);

    //根据devEUI判断设备是否存在
    boolean exists(String devEUI);

    //获取给定用户的所有设备信息
    List<UserDevice> findAllByUserid(String userid);

    // 获得设备-传感器列表详细信息，设备只需要获得传感器数量，不要求得到具体传感器值

    HashMap getDSList(String userId);

    //根据userid删除数据
    void deleteByUserid(String userid);

}
