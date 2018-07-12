package com.example.demo.Entity;


import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class User {
    @Id
    private String userid;

    private String password;
    private String flag; // flag=admin或common

    public String getUserid() {
        return userid;
    }

    public String getPassword() {
        return password;
    }

    public String getFlag() {
        return flag;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public User() {
        super();
    }

    public User(String userid, String password, String flag) {
        super();
        this.userid = userid;
        this.password = password;
        this.flag = flag;
    }

}
