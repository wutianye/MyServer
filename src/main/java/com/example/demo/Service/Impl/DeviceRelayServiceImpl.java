package com.example.demo.Service.Impl;

import com.example.demo.Entity.DeviceRelay;
import com.example.demo.Repository.DeviceRelayJpaRepository;
import com.example.demo.Service.DeviceRelayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeviceRelayServiceImpl implements DeviceRelayService{
    @Autowired
    private DeviceRelayJpaRepository deviceRelayJpaRepository;

    @Override
    public void insert(DeviceRelay deviceRelay) {
        deviceRelayJpaRepository.save(deviceRelay);
        deviceRelayJpaRepository.flush();
    }

    @Override
    public boolean exists(String devEUI, String relayType) {
        return deviceRelayJpaRepository.existsByDevEUIAndRelayType(devEUI, relayType);
    }

    @Override
    public List<DeviceRelay> findBydevEUI(String devEUI) {
        return deviceRelayJpaRepository.findDeviceRelaysByDevEUI(devEUI);
    }


    @Override
    public void deleteBydevEUI(String devEUI) {
        deviceRelayJpaRepository.deleteAllByDevEUI(devEUI);
        deviceRelayJpaRepository.flush();
    }

    @Override
    public void deleteBydevEUIAndrelayType(String devEUI, String relayType) {
        deviceRelayJpaRepository.deleteByDevEUIAndRelayType(devEUI, relayType);
        deviceRelayJpaRepository.flush();
    }
}
