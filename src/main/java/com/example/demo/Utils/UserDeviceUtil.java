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
            info.setInfo("添加设备失败！请检查设备devEui格式是否正确");
        }
        return info;
    }

    public static List getdevices(UserDeviceService userDeviceService, String userid) {
        List<UserDevice> list = userDeviceService.findAllByUserid(userid);
        return list;
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

    //更改速率字段
    public static TMessage changeFrequency(UserDeviceService userDeviceService, String devEUI, int freq) {
        UserDevice userDevice = userDeviceService.findBydevEUI(devEUI);
        int oldfreq = userDevice.getFrequency();
        userDevice.setFrequency(freq);
        try {
            userDeviceService.insert(userDevice);
        } catch (Exception e) {
            System.out.println("userDeviceService 异常！");
            return new TMessage(TMessage.CODE_FAILURE, "更新数据失败！");
        }
        //下发配置
        Info info = DataProcess.downLinkConfigHander(devEUI);
        if (!info.isResult()) {
            userDevice.setFrequency(oldfreq);
            userDeviceService.insert(userDevice);
            return new TMessage(TMessage.CODE_FAILURE, "配置下发异常！");
        }
        return new TMessage(TMessage.CODE_SUCCESS, "更新配置成功！");
    }

}
