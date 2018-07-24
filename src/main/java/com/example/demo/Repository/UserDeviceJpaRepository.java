package com.example.demo.Repository;

import com.example.demo.Entity.UserDevice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserDeviceJpaRepository extends JpaRepository<UserDevice, String> {

    List<UserDevice> findUserDevicesByUserid(String userid);

    void deleteAllByUserid(String userid);

    void deleteByDevEUI(String devEUI);

    UserDevice findUserDeviceByDevEUI(String devEUI);

}
