package com.example.demo.Service.Impl;

import com.example.demo.Entity.Data;
import com.example.demo.Repository.DataJpaRepository;
import com.example.demo.Service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataServiceImpl implements DataService{

    @Autowired
    private DataJpaRepository dataJpaRepository;

    @Override
    public Data findByDateAndDevEUIAndTypeid(String date, String devEUI, String typeid){
        return dataJpaRepository.findDataByDateAndDevEUIAndAndTypeid(date, devEUI, typeid);
    }

    @Override
    public boolean exists(String date, String devEUI, String typeid) {
        return dataJpaRepository.existsByDateAndDevEUIAndTypeid(date, devEUI, typeid);
    }

    @Override
    public void insert(Data data) {
        dataJpaRepository.save(data);
    }

}
