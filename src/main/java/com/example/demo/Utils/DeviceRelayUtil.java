package com.example.demo.Utils;

import com.example.demo.Entity.DeviceRelay;
import com.example.demo.Entity.RelaySwitch;
import com.example.demo.Entity.RelayType;
import com.example.demo.Service.DeviceRelayService;
import com.example.demo.Service.RelaySwitchService;
import com.example.demo.Service.RelayTypeService;
import com.sun.org.apache.xerces.internal.xs.datatypes.ObjectList;
import io.swagger.models.auth.In;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class DeviceRelayUtil {
    //为指定的devEUI添加一个继电器
    public static Info addsensor(DeviceRelayService deviceRelayService, String devEUI, String relayType){
        Info info = new Info();
        if (deviceRelayService.exists(devEUI, relayType)) {
            info.setResult(false);
            info.setInfo("该类型继电器已存在!");
            return info;
        }
        DeviceRelay deviceRelay = new DeviceRelay(devEUI, relayType);
        deviceRelayService.insert(deviceRelay);
        if (deviceRelayService.exists(devEUI, relayType)) {
            info.setResult(true);
            info.setInfo("添加成功！");
        } else {
            info.setResult(false);
            info.setInfo("添加失败！未知错误");
        }
        return info;
    }

    //获取指定devEUI下的所有继电器relaytype和relayName
    public static List<HashMap<String,String>> getRelay(DeviceRelayService deviceRelayService, RelayTypeService relayTypeService, String devEUI) {
        List<HashMap<String, String>> hashMapList = new ArrayList<HashMap<String, String>>();
        List<DeviceRelay> deviceRelayList = deviceRelayService.findBydevEUI(devEUI);
        for (DeviceRelay deviceRelay: deviceRelayList) {
            HashMap<String, String> hashMap = new HashMap<String, String>();
            hashMap.put("relayType", deviceRelay.getRelayType());
            RelayType relayType = relayTypeService.getaRelayType(deviceRelay.getRelayType());
            hashMap.put("relayName", relayType.getRelayName());
            hashMapList.add(hashMap);
        }
        return hashMapList;
    }

    //获取指定relayType下开关的switchId, switchName和当前开关的状态
    public static List<HashMap<String, Object>> getRelaySwitch(RelaySwitchService relaySwitchService, String devEUI, String relayType) {
        List<HashMap<String, Object>> hashMapList = new ArrayList<HashMap<String, Object>>();
        List<RelaySwitch> relaySwitchList = relaySwitchService.findByrelayType(relayType);
        //下发获取继电器状态指令，回复一个字符串如00000010，代表8位继电器当前开关状态
        //注意：此时的states结果是逆序的，要转换一下
        String state = downlinkInstruction(devEUI);
        if (state == null) {
            return null;
        }
        String states = new StringBuffer(state).reverse().toString();
        //处理二进制字符串
        //考虑到通用性，需要在DeviceRelay表中添加字段state，标识继电器当前能够使用的开关有哪些，本例为：01111111（待拓展）
        for (RelaySwitch relaySwitch : relaySwitchList) {
            //switchId从02~08,index需要先减1
            int index;
            try {
                index = Integer.parseInt(relaySwitch.getSwitchId()) - 1;
                if (index >= states.length()) {
                    throw new RuntimeException("index 越界！");
                }
            } catch (Exception e) {
                throw new RuntimeException("不能够处理index！");
            }
            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            hashMap.put("switchId", relaySwitch.getSwitchId());
            hashMap.put("switchName", relaySwitch.getSwitchName());
            if (states.substring(index, index+1).equals("0")) {
                hashMap.put("state", 0);//开关状态：关
            } else if (states.substring(index, index+1).equals("1")){
                hashMap.put("state", 1);//开关状态：开
            } else {
                continue;
            }
            hashMapList.add(hashMap);
        }
        return hashMapList;
    }

    //构造获取继电器状态指令，下发指令，等待回复的数据
    public static String downlinkInstruction(String devEUI) {
        Info info = new Info();
        String instruction = MQTTUtil.makeInstructions(Instructions.DOWNLINK_RELAY_CONTROL + Instructions.RELAY_STATE_GET + "ff");
        String topic = MQTTUtil.makeTopic(devEUI, "tx");

        int count = 3;
        while (count > 0) {
            info = MQTTUtil.publish(topic, MQTTUtil.makeData(instruction), "rstate");
            count--;
            if (info.isResult()) {
                System.out.println("获取继电器状态成功！");
                break;
            }
        }

        if (!info.isResult()) {
            System.out.println("获取继电器状态，info:" + info.getInfo());
            return null;
        }
        return info.getInfo();
    }

    //修改继电器开关状态（单个）
    public static Info changeRelaySwitch(String devEUI, String switchId, String state) {
        Info info = new Info();
        String str = "";
        if (state.equals("1")) {
            str = Instructions.RELAY_SWITCH_OPEN;
        } else if (state.equals("0")){
            str = Instructions.RELAY_SWITCH_CLOSE;
        } else {
            info.setResult(false);
            info.setInfo("无效的状态！");
            return info;
        }

        info = downlinkInstruction(devEUI, switchId, str);
        return info;
    }

    //构造修改继电器状态指令，下发指令，等待回复结果
    public static Info downlinkInstruction(String devEUI, String switchId, String state) {
        Info info = new Info();
        String instruction = MQTTUtil.makeInstructions(Instructions.DOWNLINK_RELAY_CONTROL + switchId + state + "ff");
        String topic = MQTTUtil.makeTopic(devEUI, "tx");

        int count = 3;
        while (count > 0) {
            info = MQTTUtil.publish(topic, MQTTUtil.makeData(instruction), "ack");
            count--;
            if (info.isResult()) {
                System.out.println("修改继电器开关状态成功！");
                break;
            }
        }

        return info;
    }

    //修改继电器开关状态（多个）
    public static Info changeRelaySwitch(String devEUI, String jsonstr) {
        Info info = new Info();
        //构造指令
        String str = Instructions.DOWNLINK_RELAY_CONTROL;

        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonstr);
            Iterator<String> it = jsonObject.keys();
            while(it.hasNext()) {
                // 获得key
                String key = it.next();
                String value = jsonObject.getString(key);
                String state;
                if (value.equals("1")) {
                    state = Instructions.RELAY_SWITCH_OPEN;
                } else if (value.equals("0")) {
                    state = Instructions.RELAY_SWITCH_CLOSE;
                } else {
                    info.setResult(false);
                    info.setInfo("无效的状态！");
                    return info;
                }
                str = str + key + state + "ff";
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return new Info(false, "参数不符合要求！");
        }

        String instruction = MQTTUtil.makeInstructions(str);
//        System.out.println("instruction:" + instruction);
        String topic = MQTTUtil.makeTopic(devEUI, "tx");

        int count = 3;
        while(count > 0) {
            info = MQTTUtil.publish(topic, MQTTUtil.makeData(instruction), "ack");
            count--;
            if (info.isResult()) {
                System.out.println("修改继电器开关状态成功！");
                info.setInfo("修改继电器开关状态成功！");
                break;
            }
        }

        return info;
    }
}
