package com.example.demo.Service;

import com.example.demo.Entity.SensorType;

import java.util.List;

public interface SensorTypeService {

    //获取给定typeid的一条数据
    SensorType getasensortype(String typeid);

    //获取所有的typeid、typename列表
    List<SensorType> findAll();

}
