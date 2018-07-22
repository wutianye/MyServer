package com.example.demo.Utils;

import com.example.demo.Entity.UserDevice;
import org.json.JSONException;
import org.json.JSONObject;

public class HttpInfo {
    public static String token = null;

    public static String requestURL;
    public static String getoptions;

    public static String login() {
        requestURL = Constants.HTTP_REQUESTURL_PREFIX + "/api/internal/login";
        JSONObject json = new JSONObject();
        try {
            json.put("username", Constants.HTTP_LORASERVER_USERNAME);
            json.put("password", Constants.HTTP_LORASERVER_PASSWORD);
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
        requestURL = Constants.HTTP_REQUESTURL_PREFIX + "/api/devices";
        JSONObject json = new JSONObject();
        try {
            json.put("applicationID", userDevice.getApplicationid());
            json.put("description", Constants.ADD_DEVICE_DEFAULT_DESCRIPTION);
            json.put("devEUI", userDevice.getDevEUI());
            json.put("deviceProfileID", Constants.ADD_DEVICE_DEFAULT_DEVICEPROFILEID);
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
        requestURL = Constants.HTTP_REQUESTURL_PREFIX + "/api/devices/" + devEUI;
        return HttpHelper.ByDelete(requestURL, token);
    }
}
