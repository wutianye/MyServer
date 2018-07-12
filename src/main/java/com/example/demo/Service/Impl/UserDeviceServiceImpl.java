package com.example.demo.Service.Impl;

import com.example.demo.Entity.UserDevice;
import com.example.demo.Repository.UserDeviceJpaRepository;
import com.example.demo.Service.UserDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDeviceServiceImpl implements UserDeviceService{

    @Autowired
    private UserDeviceJpaRepository userDeviceJpaRepository;

    @Override
    public void insert(UserDevice userDevice) {
        userDeviceJpaRepository.save(userDevice);
    }

    @Override
    public boolean exists(String devEUI) {
        return userDeviceJpaRepository.existsById(devEUI);
    }

    @Override
    public List<UserDevice> findAllByUserid(String userid) {
        return userDeviceJpaRepository.findUserDevicesByUserid(userid);
    }

}
