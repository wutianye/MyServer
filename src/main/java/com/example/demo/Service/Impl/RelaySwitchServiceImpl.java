package com.example.demo.Service.Impl;

import com.example.demo.Entity.RelaySwitch;
import com.example.demo.Repository.RelaySwitchJpaRepository;
import com.example.demo.Service.RelaySwitchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RelaySwitchServiceImpl implements RelaySwitchService{

    @Autowired
    private RelaySwitchJpaRepository relaySwitchJpaRepository;

    @Override
    public List<RelaySwitch> findByrelayType(String relayType) {
        return relaySwitchJpaRepository.findRelaySwitchesByRelayType(relayType);
    }

}
