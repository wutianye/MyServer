package com.example.demo.Service;

import com.example.demo.Entity.Data;

public interface DataService {

    //通过date、devEUI、typeid找到一条数据
    Data findByDateAndDevEUIAndTypeid(String date, String devEUI, String typeid);

    //通过date、devEUI、typeid判断该数据是否存在
    boolean exists(String date, String devEUI, String typeid);

    //插入一条数据
    void insert(Data data);

}
