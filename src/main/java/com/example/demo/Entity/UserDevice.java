package com.example.demo.Entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class UserDevice {
    @Id
    private String devEUI;

    private String devname;
    private String userid;
    private String applicationid;


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

    public UserDevice(String devEUI, String devname, String userid, String applicationid) {
        super();
        this.devEUI = devEUI;
        this.devname = devname;
        this.userid = userid;
        this.applicationid = applicationid;
    }

}
