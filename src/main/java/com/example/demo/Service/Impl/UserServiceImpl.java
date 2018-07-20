package com.example.demo.Service.Impl;

import com.example.demo.Entity.User;
import com.example.demo.Repository.UserJpaRepository;
import com.example.demo.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Max;
import java.util.HashMap;
import java.util.List;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private UserDeviceServiceImpl userDeviceService; // 根据user查看用户所拥有的传感器
//    private User

    @Override
    public void insert(User user) {
        userJpaRepository.save(user);
    }

    @Override
    public boolean exists(String userid) {
        return userJpaRepository.existsById(userid);
    }

    @Override
    public User findByUserid(String userid) {
        return userJpaRepository.findUserByUserid(userid);
    }

    @Override
    public HashMap getAllUserInfo(String userId) {

        List<User> userList = userJpaRepository.findAll();// 找到所有的user
        HashMap res = new HashMap();
        res.put("userNum", userList.size()); // 获得用户的列表;
        userList.forEach((user -> {
            user.setPassword("no privilige!");
        }));
        res.put("userInfo",userList);
        return res;
    }

    // 修改密码
    @Override
    public Boolean modifyPassword() {
        return null;
    }


}
