package com.example.demo.Entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class SensorType {

    @Id
    private String typeid;

    private String typename;

    public String getTypeid() {
        return typeid;
    }

    public String getTypename() {
        return typename;
    }

    public void setTypeid(String typeid) {
        this.typeid = typeid;
    }

    public void setTypename(String typename) {
        this.typename = typename;
    }

    public SensorType() {
        super();
    }

    public SensorType(String typeid, String typename) {
        super();
        this.typeid = typeid;
        this.typename = typename;
    }

}
