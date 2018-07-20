package com.example.demo.Service.Impl;

import com.example.demo.Entity.UserToken;
import com.example.demo.Repository.UserTokenJpaRepository;
import com.example.demo.Service.UserTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
public class UserTokenServiceImpl implements UserTokenService {
    @Autowired
    private UserTokenJpaRepository userTokenJpaRepository;

    @Override
    public void insert(UserToken userToken) {
        userTokenJpaRepository.save(userToken);
        userTokenJpaRepository.flush();
    }

    @Override
    public UserToken findByToken(String token) {
        return userTokenJpaRepository.findByWebtokenOrApptoken(token, token);
    }

    @Override
    public boolean exists(String userid) {
        return userTokenJpaRepository.existsById(userid);
    }

    @Override
    public UserToken findByUserid(String userid) {
        return userTokenJpaRepository.findByUserid(userid);
    }

    @Override
    public void deleteByUserid(String userid) {
        userTokenJpaRepository.deleteById(userid);
        userTokenJpaRepository.flush();
    }
}
