package com.example.demo.Service.Impl;

import com.example.demo.Entity.DeviceSensor;
import com.example.demo.Repository.DeviceSensorJpaRepository;
import com.example.demo.Service.DeviceSensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeviceSensorServiceImpl implements DeviceSensorService{

    @Autowired
    private DeviceSensorJpaRepository deviceSensorJpaRepository;


    @Override
    public void insert(DeviceSensor deviceSensor) {
        deviceSensorJpaRepository.saveAndFlush(deviceSensor);
    }

    @Override
    public boolean exists(String devEUI, String typeid) {
        return deviceSensorJpaRepository.existsByDevEUIAndTypeid(devEUI, typeid);
    }

    @Override
    public List<DeviceSensor> findBydevEUI(String devEUI) {
        return deviceSensorJpaRepository.findDeviceSensorsByDevEUI(devEUI);
    }

    @Override
    public DeviceSensor findBydevEUIAndTypeid(String devEUI, String typeid) {
        return deviceSensorJpaRepository.findDeviceSensorByDevEUIAndTypeid(devEUI, typeid);
    }

    @Override
    public void deleteBydevEUI(String devEUI) {
        deviceSensorJpaRepository.deleteAllByDevEUI(devEUI);
        deviceSensorJpaRepository.flush();
    }

    @Override
    public void deleteBydevEUIAndTypeid(String devEUI, String typeid) {
        deviceSensorJpaRepository.deleteByDevEUIAndTypeid(devEUI, typeid);
        deviceSensorJpaRepository.flush();
    }
}
