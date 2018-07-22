package com.example.demo.Entity;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class DeviceRelay {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String devEUI;
    private String relayType;

    public String getDevEUI() {
        return devEUI;
    }

    public void setDevEUI(String devEUI) {
        this.devEUI = devEUI;
    }

    public String getRelayType() {
        return relayType;
    }

    public void setRelayType(String relayType) {
        this.relayType = relayType;
    }


    public DeviceRelay(String devEUI, String relayType) {
        super();
        this.devEUI = devEUI;
        this.relayType = relayType;
    }

    public DeviceRelay() {
        super();
    }
}
