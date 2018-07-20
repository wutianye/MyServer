package com.example.demo.Service;

import com.example.demo.Entity.RelayType;

import java.util.List;

public interface RelayTypeService {

    //获取给定relaytype 的一条数据
    RelayType getaRelayType(String relayType);


    //获取所有relaytype, relayname列表
    List<RelayType> findAll();
}
