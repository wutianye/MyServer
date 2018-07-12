package com.example.demo.Service.Impl;

import com.example.demo.Entity.User;
import com.example.demo.Repository.UserJpaRepository;
import com.example.demo.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserJpaRepository userJpaRepository;

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
}
