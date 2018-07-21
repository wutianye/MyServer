package com.example.demo.Service;

import com.example.demo.Entity.Data;

import java.util.List;

public interface DataService {

    //通过date、devEUI、typeid找到一条数据（某一条数据）
    Data findByDateAndDevEUIAndTypeid(String date, String devEUI, String typeid);

    //通过date、devEUI、typeid判断该数据是否存在
    boolean exists(String date, String devEUI, String typeid);

    //插入一条数据
    void insert(Data data);

    //查询符合%date%且devEUI、typeid的数据列表(某日数据)
    List<Data> findByDateLikeAndDevEUIAndTypeid(String datepattern, String devEUI, String typeid);

    //查询给定date1到date2之间的devEUI、typeid的数据列表（一或多日数据）
    List<Data> findByDateBetweenDate1AndDate2AndDevEUIAndTypeid(String date1, String date2, String devEUI, String typeid);

    //根据devEUI删除数据
    void deleteBydevEUI(String devEUI);

    //根据devEUI、typeid删除数据
    void deleteBydevEUIAndTypeid(String devEUI, String typeid);

    //根据devEUI、typeid查询数据
    List<Data> findBydevEUIAndTypeid(String devEUI, String typeid);

    //根据devEUI查询数据
    List<Data> findBydevEUI(String devEUI);

}
