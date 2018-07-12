package com.example.demo.Entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class UserToken {
    @Id
    private String userid;

    private String token;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserToken() {
        super();
    }

    public UserToken(String userid, String token) {
        super();
        this.userid = userid;
        this.token = token;
    }
}
