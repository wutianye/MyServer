package com.example.demo.Service;

import com.example.demo.Entity.RelaySwitch;

import java.util.List;

public interface RelaySwitchService {

    //通过relaytype查询所有开关
    List<RelaySwitch> findByrelayType(String relayType);
}
