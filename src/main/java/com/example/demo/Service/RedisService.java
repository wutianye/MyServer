package com.example.demo.Service;

import java.util.Set;

public interface RedisService {

    //判断key是否存在
    boolean hasKey(String key);

    //根据key删除key和value
    void deleteByKey(String key);

    //根据key获取value值
    String getValue(String key);

    //插入数据
    void setValue(String key,String value);

    //获取给定pattern的keys集合
    Set<String> getKeysByPattern(String pattern);
}
