package com.example.demo.Service;

import com.example.demo.Entity.UserDevice;

import java.util.List;

public interface UserDeviceService {

    //添加一个设备
    void insert(UserDevice userDevice);

    //根据devEUI判断设备是否存在
    boolean exists(String devEUI);

    //获取给定用户的所有设备信息
    List<UserDevice> findAllByUserid(String userid);

}
