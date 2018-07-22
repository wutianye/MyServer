package com.example.demo.Repository;

import com.example.demo.Entity.DeviceRelay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeviceRelayJpaRepository extends JpaRepository<DeviceRelay, String>{

    boolean existsByDevEUIAndRelayType(String devEUI, String relayType);

    List<DeviceRelay> findDeviceRelaysByDevEUI(String devEUI);

    void deleteAllByDevEUI(String devEUI);

    void deleteByDevEUIAndRelayType(String devEUI, String relayType);
}
