package com.example.demo.Utils;

import com.example.demo.Entity.UserDevice;
import org.json.JSONException;
import org.json.JSONObject;

public class HttpInfo {
    public static String token = null;

    public static String requestURL;
    public static String getoptions;

    public static String login() {
        requestURL = "https://www.liuyunxing.cn:8080/api/internal/login";
        JSONObject json = new JSONObject();
        try {
            json.put("username", "test2");//指定与LoraServer连接的用户为test2
            json.put("password", "123456");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String result = HttpHelper.ByPost(requestURL, json, token);
        if (result == null) {
            return "登陆LoRaServer失败！";
        } else {
            try {
                JSONObject newjson = new JSONObject(result);
                if (newjson.has("jwt")) {
                    token = newjson.getString("jwt");
                    return "登陆LoRaServer成功！";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return "登陆LoRaServer失败！";
//        return HttpHelper.ByPost(requestURL, json, token);
    }

    public static boolean addDevice(UserDevice userDevice) {
        requestURL = "https://www.liuyunxing.cn:8080/api/devices";
        JSONObject json = new JSONObject();
        try {
            json.put("applicationID", "4"); //此处userDevice.getApplicationid()
            json.put("description", "这是描述");
            json.put("devEUI", userDevice.getDevEUI());
            json.put("deviceProfileID", "0391774c-8fcb-46b1-a225-9faef7d57fe1");
            json.put("name", userDevice.getDevname());
            json.put("skipFCntCheck", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String result = HttpHelper.ByPost(requestURL, json, token);
        if (result == null) {
            return false;
        }
        return true;
    }

    public static boolean deleteDevice(String devEUI) {
        requestURL = "https://www.liuyunxing.cn:8080/api/devices/" + devEUI;
        return HttpHelper.ByDelete(requestURL, token);
    }
}
