package com.example.demo.Repository;

import com.example.demo.Entity.RelaySwitch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RelaySwitchJpaRepository extends JpaRepository<RelaySwitch, String> {
    List<RelaySwitch> findRelaySwitchesByRelayType(String relayType);
}
