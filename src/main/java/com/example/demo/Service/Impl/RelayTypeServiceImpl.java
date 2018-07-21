package com.example.demo.Service.Impl;

import com.example.demo.Entity.RelayType;
import com.example.demo.Repository.RelayTypeJpaRepository;
import com.example.demo.Service.RelayTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RelayTypeServiceImpl implements RelayTypeService {
    @Autowired
    private RelayTypeJpaRepository relayTypeJpaRepository;

    @Override
    public RelayType getaRelayType(String relayType) {
        return relayTypeJpaRepository.findRelayTypeByRelayType(relayType);
    }

    @Override
    public List<RelayType> findAll() {
        return relayTypeJpaRepository.findAll();
    }

}
