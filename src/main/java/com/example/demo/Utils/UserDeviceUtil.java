package com.example.demo.Utils;

import com.example.demo.Entity.UserDevice;
import com.example.demo.Service.UserDeviceService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserDeviceUtil {
    final public static String APPLICATIONID = "5";

    public static Info adddevice(UserDeviceService userDeviceService, String devEUI, String devname, String userid, String longitude, String latitude, String address) {
        UserDevice userDevice = new UserDevice(devEUI, devname, userid,new UserDeviceUtil().APPLICATIONID, longitude, latitude, address);
        Info info = new Info();
        if (userDeviceService.exists(devEUI)) {
            info.setResult(false);
            info.setInfo("设备已存在！");
            return info;
        }
        boolean result = HttpInfo.addDevice(userDevice);
        if (result) {
            userDeviceService.insert(userDevice);
            if (userDeviceService.exists(devEUI)) {
                info.setResult(true);
                info.setInfo("添加设备成功！");
            }
        } else {
            info.setResult(false);
            info.setInfo("添加设备失败！未知错误");
        }
        return info;
    }

    public static List<HashMap<String, String>> getdevices(UserDeviceService userDeviceService, String userid) {
        List<UserDevice> list = userDeviceService.findAllByUserid(userid);
        return toHashlist(list);
    }

    //list<UserDevice>转list<HashMap<String,String>>
    public static List<HashMap<String,String>> toHashlist(List<UserDevice> userDeviceList) {
        List<HashMap<String, String>> hashMapList= new ArrayList<HashMap<String,String>>();
        for (UserDevice userDevice : userDeviceList) {
            HashMap<String,String> hashMap = new HashMap<String, String>();
            hashMap.put("devEUI", userDevice.getDevEUI());
            hashMap.put("devname", userDevice.getDevname());
            hashMapList.add(hashMap);
        }
        return hashMapList;
    }


}
