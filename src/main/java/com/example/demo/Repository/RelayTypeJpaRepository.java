package com.example.demo.Repository;

import com.example.demo.Entity.RelayType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RelayTypeJpaRepository extends JpaRepository<RelayType, String> {

    RelayType findRelayTypeByRelayType(String relayType);
}
