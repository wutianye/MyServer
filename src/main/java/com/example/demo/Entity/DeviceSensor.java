package com.example.demo.Entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class DeviceSensor {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    private String devEUI;
    private String typeid;

    private boolean state;

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public String getDevEUI() {
        return devEUI;
    }

    public String getTypeid() {
        return typeid;
    }

    public void setDevEUI(String devEUI) {
        this.devEUI = devEUI;
    }

    public void setTypeid(String typeid) {
        this.typeid = typeid;
    }

    public DeviceSensor() {
        super();
    }

    public DeviceSensor(String devEUI, String typeid, boolean state) {
        super();
        this.devEUI = devEUI;
        this.typeid = typeid;
        this.state = state;
    }
}
