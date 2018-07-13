package com.example.demo.Entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class UserToken {
    @Id
    private String userid;

    private String webtoken;
    private String apptoken;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getWebtoken() {
        return webtoken;
    }

    public void setWebtoken(String webtoken) {
        this.webtoken = webtoken;
    }

    public String getApptoken() {
        return apptoken;
    }

    public void setApptoken(String apptoken) {
        this.apptoken = apptoken;
    }

    public UserToken() {
        super();
    }

    public UserToken(String userid, String webtoken, String apptoken) {
        super();
        this.userid = userid;
        this.webtoken = webtoken;
        this.apptoken = apptoken;
    }
}
