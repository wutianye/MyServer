package com.example.demo.Entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class RelaySwitch {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String relayType;
    private String  switchId; //连接的继电器开关的序号
    private String switchName;

    public String getRelayType() {
        return relayType;
    }

    public void setRelayType(String relayType) {
        this.relayType = relayType;
    }

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

    public RelaySwitch(String relayType, String  switchId, String switchName) {
        super();
        this.relayType = relayType;
        this.switchId = switchId;
        this.switchName = switchName;
    }

    public RelaySwitch() {
        super();
    }
}
