package com.example.demo.Service;

import com.example.demo.Entity.UserToken;

public interface UserTokenService {
    //插入一条数据/更新一条数据
    void insert(UserToken userToken);

    //通过token获取一条数据
    UserToken findByToken(String token);

    //判断userid对应的数据是都存在
    boolean exists(String userid);

    //获取一条数据
    UserToken findByUserid(String userid);

    //根据userid删除一条数据
    void deleteByUserid(String userid);

}
