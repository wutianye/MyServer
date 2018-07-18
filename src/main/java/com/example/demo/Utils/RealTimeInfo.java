package com.example.demo.Utils;

import java.util.HashMap;

public class RealTimeInfo {

    private String date;
    private String devEUI;
    private String typeid;
    private String choice;
    private String value;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDevEUI() {
        return devEUI;
    }

    public void setDevEUI(String devEUI) {
        this.devEUI = devEUI;
    }

    public String getTypeid() {
        return typeid;
    }

    public void setTypeid(String typeid) {
        this.typeid = typeid;
    }

    public String getChoice() {
        return choice;
    }

    public void setChoice(String choice) {
        this.choice = choice;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public RealTimeInfo() {
        super();
    }

    public RealTimeInfo(String date, String devEUI, String typeid, String choice, String value) {
        this.date = date;
        this.devEUI = devEUI;
        this.typeid = typeid;
        this.choice = choice;
        this.value = value;
    }

    public HashMap<String, String> toHashMap() {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("date", this.date);
        hashMap.put("devEUI", this.devEUI);
        hashMap.put("typeid", this.typeid);
        hashMap.put("choice", this.choice);
        hashMap.put("value", this.value);
        return hashMap;
    }
}
