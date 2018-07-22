package com.example.demo.Service;

import com.example.demo.Entity.User;
import com.example.demo.Utils.TMessage;

import java.util.HashMap;
import java.util.List;


public interface UserService {

    //注册:插入一条数据
    void insert(User user);

    //根据userid判断用户是否存在
    boolean exists(String userid);

    //根据userid查询信息
    User findByUserid(String userid);

    HashMap getAllUserInfo(String userId);

    TMessage modifyPassword(String userId, String modUserId, String password);

    TMessage modifyUserRole(String userId, String modUserId, String role);

    //根据userid删除一条用户的信息
    void deleteByUserid(String userid);
    //获得所有用户的信息,以树形显示
    TMessage getTreeInfo(String userId);
    //获得制定用户的所有设备信息
    TMessage getTreeDevice(String userId, String aimUserId);

}
