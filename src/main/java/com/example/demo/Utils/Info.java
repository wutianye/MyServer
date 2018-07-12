package com.example.demo.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class Info {
    private boolean result;
    private String info;
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Info(boolean result, String info) {
        super();
        this.result = result;
        this.info = info;
    }

    public Info() {
        super();
    }

    public String toJSONString() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("result", this.isResult());
            jsonObject.put("info", this.getInfo());
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String toJSONStringWithToken() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("result", this.isResult());
            jsonObject.put("info", this.getInfo());
            jsonObject.put("token", this.getToken());
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
