package com.example.demo.Repository;

import com.example.demo.Entity.DeviceSensor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeviceSensorJpaRepository extends JpaRepository<DeviceSensor, String>{

    boolean existsByDevEUIAndTypeid(String devEUI, String typeid);

    List<DeviceSensor> findDeviceSensorsByDevEUI(String devEUI);

    DeviceSensor findDeviceSensorByDevEUIAndTypeid(String devEUI, String typeid);
}
