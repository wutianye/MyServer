package com.example.demo.Utils;


import com.example.demo.Entity.DeviceSensor;
import com.example.demo.Service.DeviceSensorService;
import com.example.demo.Service.Impl.DeviceSensorServiceImpl;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

//处理安卓端实时数据
public class RealTimeUtil {
    private static DeviceSensorService deviceSensorService = SpringBeanFactoryUtil.getBean(DeviceSensorServiceImpl.class);

    public static List<RealTimeInfo> realTimeInfoList = new ArrayList<RealTimeInfo>();

    //hashMap里 key字段标记realTimeInfoList是否包含某条数据，需要对该数据进行更新
    // Boolean类型的字段暂无处理
    public static HashMap<String, Boolean> hashMap = new HashMap<String, Boolean>();

    //获取实时数据
    public static List<JSONObject> getRealTimeData(String devEUI) {
        List<JSONObject> jsonObjectList = new ArrayList<JSONObject>();
        String date = DataProcess.getNowDate("yyyyMMddHHmmss");
        //根据devEUI查询当前设备下的传感器
        List<DeviceSensor> deviceSensorList = deviceSensorService.findBydevEUI(devEUI);
        for (DeviceSensor deviceSensor : deviceSensorList) {
            if (deviceSensor.getState().equals("1")) {
                String key = devEUI + "_" + deviceSensor.getTypeid();

                if (hashMap.containsKey(key)) {
                    for (RealTimeInfo realTimeInfo : realTimeInfoList) {
                        if (realTimeInfo.getDevEUI().equals(devEUI) && realTimeInfo.getTypeid().equals(deviceSensor.getTypeid())) {
                            if (!dateBetween(date, realTimeInfo.getDate())) {
                                realTimeInfo.setValue("暂无数据");
                            }
                            jsonObjectList.add(realTimeInfo.toJSONObject());
                        }
                    }
                }
            }
        }
        return jsonObjectList;
    }

    //实时数据存储
    public static void saveRealTimeData(String devEUI, String forcheck) {
        String date = DataProcess.getNowDate("yyyyMMddHHmmss");

        for (int index = 0; index < forcheck.length(); ) {
            if (index + 4 >= forcheck.length()) {
                throw new IndexOutOfBoundsException();
            }
            String typeid = forcheck.substring(index, index + 2);
            int length = Integer.parseInt(forcheck.substring(index + 2, index + 4), 16);
            index += 4;
            switch (typeid) {
                case Instructions.SENSOR_WIND:
                    if (length == 2 && !forcheck.substring(index, index+4).equals("ffff")) {
                        float fengsu = (float) (Integer.parseInt(forcheck.substring(index, index + 4), 16) / 100.0);
//                        System.out.println("风速：" + fengsu + "m/s");
                        String key = devEUI + "_" + typeid;
                        if (!hashMap.containsKey(key)) {
                            hashMap.put(key, true);
                            String value = "" + fengsu;
                            RealTimeInfo realTimeInfo = new RealTimeInfo(date, devEUI, typeid, "风速", value);
                            realTimeInfoList.add(realTimeInfo);
                        } else {
                            RealTimeInfo realTimeInfo;
                            for (int i = 0; i < realTimeInfoList.size(); i++) {
                                realTimeInfo = realTimeInfoList.get(i);
                                if (realTimeInfo.getDevEUI().equals(devEUI) && realTimeInfo.getTypeid().equals(typeid)) {
                                    realTimeInfo.setDate(date);
                                    realTimeInfo.setValue("" + fengsu);
                                    realTimeInfoList.set(i, realTimeInfo);
                                    break;
                                }
                            }
                        }
                    }
                    break;
                case Instructions.SENSOR_GTH:
                    if (length == 6 && !forcheck.substring(index, index + 4).equals("ffff") &&
                            !forcheck.substring(index + 4, index + 8).equals("ffff") &&
                            !forcheck.substring(index + 8, index + 12).equals("ffff")){
                        float qiti = (float) (Integer.parseInt(forcheck.substring(index, index + 4), 16) / 10.0);
                        float wendu = (float) (Integer.parseInt(forcheck.substring(index + 4, index + 8), 16) / 10.0);
                        float shidu = (float) (Integer.parseInt(forcheck.substring(index + 8, index + 12), 16) / 100.0);
//                        System.out.println("气体：" + qiti + "\n温度：" + wendu + "0C\n湿度：" + shidu);

                        String key = devEUI + "_" + typeid;
                        if (!hashMap.containsKey(key)) {
                            hashMap.put(key, true);
                            RealTimeInfo realTimeInfo = new RealTimeInfo(date, devEUI, typeid, "气体浓度", "" + qiti);
                            RealTimeInfo realTimeInfo1 = new RealTimeInfo(date, devEUI, typeid, "温度", "" + wendu);
                            RealTimeInfo realTimeInfo2 = new RealTimeInfo(date, devEUI, typeid, "湿度", "" + shidu);
                            realTimeInfoList.add(realTimeInfo);
                            realTimeInfoList.add(realTimeInfo1);
                            realTimeInfoList.add(realTimeInfo2);
                        } else {
                            RealTimeInfo realTimeInfo;
                            int count = 3;
                            for (int i = 0; i < realTimeInfoList.size(); i++) {
                                if (count <= 0) {
                                    break;
                                }
                                realTimeInfo = realTimeInfoList.get(i);
                                if (realTimeInfo.getDevEUI().equals(devEUI) && realTimeInfo.getTypeid().equals(typeid)) {
                                    if (realTimeInfo.getChoice().equals("温度")){
                                        realTimeInfo.setDate(date);
                                        realTimeInfo.setValue("" + wendu);
                                        realTimeInfoList.set(i, realTimeInfo);
                                        count--;
                                        continue;
                                    }
                                    if (realTimeInfo.getChoice().equals("湿度")) {
                                        realTimeInfo.setDate(date);
                                        realTimeInfo.setValue("" + shidu);
                                        realTimeInfoList.set(i, realTimeInfo);
                                        count--;
                                        continue;
                                    }
                                    if (realTimeInfo.getChoice().equals("气体浓度")) {
                                        realTimeInfo.setDate(date);
                                        realTimeInfo.setValue("" + qiti);
                                        realTimeInfoList.set(i, realTimeInfo);
                                        count--;
                                        continue;
                                    }
                                }
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
            index += length * 2;
        }
//        System.out.println("Android实时数据存储成功！");
    }

    //判断两时间相差是否多于30s，多于30s则认为无最新数据
    private static boolean dateBetween(String nowdate, String olddate) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        try {
            Date now = df.parse(nowdate);
            Date old = df.parse(olddate);
            if (((now.getTime() - old.getTime())/1000) < 30) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }


}
