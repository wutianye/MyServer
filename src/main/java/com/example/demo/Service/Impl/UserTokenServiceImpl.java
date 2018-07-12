package com.example.demo.Service.Impl;

import com.example.demo.Entity.UserToken;
import com.example.demo.Repository.UserTokenJpaRepository;
import com.example.demo.Service.UserTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserTokenServiceImpl implements UserTokenService {
    @Autowired
    private UserTokenJpaRepository userTokenJpaRepository;

    @Override
    public void insert(UserToken userToken) {
        userTokenJpaRepository.save(userToken);
    }

    @Override
    public UserToken findByToken(String token) {
        return userTokenJpaRepository.findUserTokenByToken(token);
    }
}
