package com.example.demo.Repository;

import com.example.demo.Entity.Data;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DataJpaRepository extends JpaRepository<Data, String>{

    Data findDataByDateAndDevEUIAndAndTypeid(String date, String devEUI, String typeid);

    boolean existsByDateAndDevEUIAndTypeid(String date, String devEUI, String typeid);

    List<Data> findAllByDateLikeAndDevEUIAndTypeid(String datepattern, String devEUI, String typeid);

    List<Data> findAllByDevEUIAndTypeidAndDateBetween(String devEUI, String typeid, String date1, String date2);

    void deleteAllByDevEUI(String devEUI);

    void deleteAllByDevEUIAndTypeid(String devEUI, String typeid);
}
