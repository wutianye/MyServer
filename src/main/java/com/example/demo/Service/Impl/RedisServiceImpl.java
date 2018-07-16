package com.example.demo.Service.Impl;

import com.example.demo.Service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisServiceImpl implements RedisService{
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    //判断key是否存在
    @Override
    public boolean hasKey(String key){
        return redisTemplate.hasKey(key);
    }

    //根据key删除key和value
    @Override
    public void deleteByKey(String key){
        redisTemplate.delete(key);
    }

    //根据key获取value值
    @Override
    public String getValue(String key){
        return (String) redisTemplate.opsForValue().get(key);
    }

    //插入数据
    @Override
    public void setValue(String key,String value){
        redisTemplate.opsForValue().set(key,value);
    }

}
