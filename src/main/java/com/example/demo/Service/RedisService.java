package com.example.demo.Service;

public interface RedisService {

    //判断key是否存在
    boolean hasKey(String key);

    //根据key删除key和value
    void deleteByKey(String key);

    //根据key获取value值
    String getValue(String key);

    //插入数据
    void setValue(String key,String value);
}
