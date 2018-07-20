package com.example.demo.Entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class RelayType {
    @Id
    private String relayType;

    private String relayName;

    public String getRelayType() {
        return relayType;
    }

    public void setRelayType(String relayType) {
        this.relayType = relayType;
    }

    public String getRelayName() {
        return relayName;
    }

    public void setRelayName(String relayName) {
        this.relayName = relayName;
    }

    public RelayType() {
        super();
    }

    public RelayType(String relayType, String relayName) {
        super();
        this.relayType = relayType;
        this.relayName = relayName;
    }
}
