package com.example.demo.Entity;

import com.example.demo.Utils.Constants;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class UserDevice {
    @Id
    private String devEUI;

    private String devname;
    private String userid;
    private String applicationid;
    private String longitude;//经度
    private String latitude;//纬度
    private String address;
    private int frequency;//频率


    public String getApplicationid() {
        return applicationid;
    }

    public String getUserid() {
        return userid;
    }

    public String getDevEUI() {
        return devEUI;
    }

    public void setApplicationid(String applicationid) {
        this.applicationid = applicationid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setDevEUI(String devEUI) {
        this.devEUI = devEUI;
    }

    public String getDevname() {
        return devname;
    }

    public void setDevname(String devname) {
        this.devname = devname;
    }

    public UserDevice() {
        super();
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }


    public UserDevice(String devEUI, String devname, String userid, String applicationid, String longitude, String latitude, String address) {
        this.devEUI = devEUI;
        this.devname = devname;
        this.userid = userid;
        this.applicationid = applicationid;
        this.longitude = longitude;
        this.latitude = latitude;
        this.address = address;
        this.frequency = Constants.DEFAULT_FREQUENCY;
    }
}
