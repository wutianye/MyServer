package com.example.demo.Service;

import com.example.demo.Entity.UserToken;

public interface UserTokenService {
    //插入一条数据/更新一条数据
    void insert(UserToken userToken);

    //通过token获取一条数据（需要userid）
    UserToken findByToken(String token);

}
