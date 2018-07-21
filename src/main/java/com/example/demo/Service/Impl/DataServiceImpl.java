package com.example.demo.Service.Impl;

import com.example.demo.Entity.Data;
import com.example.demo.Repository.DataJpaRepository;
import com.example.demo.Service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataServiceImpl implements DataService{

    @Autowired
    private DataJpaRepository dataJpaRepository;

    @Override
    public Data findByDateAndDevEUIAndTypeid(String date, String devEUI, String typeid){
        return dataJpaRepository.findDataByDateAndDevEUIAndTypeid(date, devEUI, typeid);
    }

    @Override
    public boolean exists(String date, String devEUI, String typeid) {
        return dataJpaRepository.existsByDateAndDevEUIAndTypeid(date, devEUI, typeid);
    }

    @Override
    public void insert(Data data) {
        dataJpaRepository.saveAndFlush(data);
    }

    @Override
    public List<Data> findByDateLikeAndDevEUIAndTypeid(String datepattern, String devEUI, String typeid){
        return dataJpaRepository.findAllByDateLikeAndDevEUIAndTypeid(datepattern, devEUI, typeid);
    }

    @Override
    public List<Data> findByDateBetweenDate1AndDate2AndDevEUIAndTypeid(String date1, String date2, String devEUI, String typeid) {
        return dataJpaRepository.findAllByDevEUIAndTypeidAndDateBetween(devEUI, typeid, date1, date2);
    }

    @Override
    public void deleteBydevEUI(String devEUI) {
        dataJpaRepository.deleteAllByDevEUI(devEUI);
        dataJpaRepository.flush();
    }

    @Override
    public void deleteBydevEUIAndTypeid(String devEUI, String typeid) {
        dataJpaRepository.deleteAllByDevEUIAndTypeid(devEUI, typeid);
        dataJpaRepository.flush();
    }

    @Override
    public List<Data> findBydevEUIAndTypeid(String devEUI, String typeid) {
        return dataJpaRepository.findAllByDevEUIAndTypeid(devEUI, typeid);
    }

    @Override
    public List<Data> findBydevEUI(String devEUI) {
        return dataJpaRepository.findAllByDevEUI(devEUI);
    }
}
