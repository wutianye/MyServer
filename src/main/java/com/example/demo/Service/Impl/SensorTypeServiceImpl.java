package com.example.demo.Service.Impl;

import com.example.demo.Entity.SensorType;
import com.example.demo.Repository.SensorTypeJpaRepository;
import com.example.demo.Service.SensorTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SensorTypeServiceImpl  implements SensorTypeService{

    @Autowired
    private SensorTypeJpaRepository sensorTypeJpaRepository;


    @Override
    public SensorType getasensortype(String typeid) {
        return sensorTypeJpaRepository.findSensorTypeByTypeid(typeid);
    }

    @Override
    public List<SensorType> findAll() {
        return sensorTypeJpaRepository.findAll();
    }

}
