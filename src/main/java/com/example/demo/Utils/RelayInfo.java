package com.example.demo.Utils;

public class RelayInfo {
    private String switchId;
    private String switchName;
    private String state;

    public String getSwitchId() {
        return switchId;
    }

    public void setSwitchId(String switchId) {
        this.switchId = switchId;
    }

    public String getSwitchName() {
        return switchName;
    }

    public void setSwitchName(String switchName) {
        this.switchName = switchName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public RelayInfo(String switchId, String switchName, String state) {
        super();
        this.switchId = switchId;
        this.switchName = switchName;
        this.state = state;
    }

    public RelayInfo() {
        super();
    }
}
