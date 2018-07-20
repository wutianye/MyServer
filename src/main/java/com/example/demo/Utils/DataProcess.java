package com.example.demo.Utils;

import com.alibaba.fastjson.JSON;
import com.example.demo.Entity.Data;
import com.example.demo.Service.DataService;
import com.example.demo.Service.Impl.DataServiceImpl;
import com.example.demo.Service.Impl.RedisServiceImpl;
import com.example.demo.Service.RedisService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.*;


public class DataProcess {

    private static DataService dataService = SpringBeanFactoryUtil.getBean(DataServiceImpl.class);
    private static RedisService redisService = SpringBeanFactoryUtil.getBean(RedisServiceImpl.class);

    public static String currentTime = "";

    public static HashMap<String, Boolean> hashMap = new HashMap<String, Boolean>();

    static final Base64.Decoder decoder = Base64.getDecoder();
    static final Base64.Encoder encoder = Base64.getEncoder();

    public static void getDataFromTopicAndPayLoad(String topic, String payload) {
        String str = topic.substring(topic.length() - 2, topic.length());
        if (str.equals("rx")) {
            try {
                JSONObject jsonObject = new JSONObject(payload);
                String devEUI = jsonObject.getString("devEUI");

                //byte[] 转 hex string
                byte[] bytes = decoder.decode(jsonObject.getString("data"));
                StringBuilder buf = new StringBuilder();
                for (byte b : bytes) {
                    buf.append(String.format("%02x", new Integer(b & 0xff)));
                }
                System.out.println(buf.toString());

                String date = getNowDate("yyyyMMddHH");
                if (!date.equals(currentTime)) {
                    currentTime = date;
                    hashMap.clear();
                }
                if (hashMap.containsKey(devEUI)){
                    if (hashMap.get(devEUI)) {
                        return;
                    }
                } else {
                    hashMap.put(devEUI, false);
                }
                //分析并存储数据
                hexStringAnalysis(buf.toString(), devEUI, date);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    public static void hexStringAnalysis(String hexstr, String devEUI, String date) {
        if (hexstr.length() <= 4 ) {
            System.out.println("数据格式有误！");
//            throw new IndexOutOfBoundsException();
            return ;
        }
        //CRC 校验
        String CRCstr = hexstr.substring(hexstr.length() - 4, hexstr.length());
        String forcheck = hexstr.substring(0, hexstr.length() - 4);
        if (CRC16Modbus.checkCRC16(forcheck, CRCstr)) {
            System.out.println("CRC校验成功！");
        } else {
            System.out.println("CRC校验失败！");
            return ;
        }
        if (forcheck.length() <= 6) {
//            throw new IndexOutOfBoundsException();
            return;
        }
        String type = forcheck.substring(0, 2);

        if (!type.equals(Instructions.UPLINK_SENSOR_DATA)) {
            return;
        }

        forcheck = forcheck.substring(2, forcheck.length());
        boolean result = true;
        //分析数据
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
                        System.out.println("风速：" + fengsu + "m/s");
                        //存数据
                        String value = "" + fengsu;
                        Data data = new Data(date, devEUI, typeid, value);
                        try {
                            dataService.insert(data);
                        } catch (Exception e) {
                            System.out.println("dataService 异常");
                        }

                        //存入redis数据库
                        String key = date + "_" + devEUI + "_" + typeid;
                        try {
                            redisService.setValue(key, value);
                        } catch (Exception e) {
                            System.out.println("redisService 异常");
                        }

                    } else {
                        result = false;
                    }
                    break;
                case Instructions.SENSOR_GTH:
                    if (length == 6 && !forcheck.substring(index, index + 4).equals("ffff") &&
                            !forcheck.substring(index + 4, index + 8).equals("ffff") &&
                            !forcheck.substring(index + 8, index + 12).equals("ffff")){
                        float qiti = (float) (Integer.parseInt(forcheck.substring(index, index + 4), 16) / 10.0);
                        float wendu = (float) (Integer.parseInt(forcheck.substring(index + 4, index + 8), 16) / 10.0);
                        float shidu = (float) (Integer.parseInt(forcheck.substring(index + 8, index + 12), 16) / 100.0);
                        System.out.println("气体：" + qiti + "\n温度：" + wendu + "0C\n湿度：" + shidu);
                        //存数据
                        String value = qiti + "_" + wendu + "_" + shidu;
                        Data data = new Data(date, devEUI, typeid, value);
                        try {
                            dataService.insert(data);
                        } catch (Exception e) {
                            System.out.println("dataService 异常");
                        }

                        //存入redis数据库
                        String key = date + "_" + devEUI + "_" + typeid;
                        try {
                            redisService.setValue(key, value);
                        } catch (Exception e) {
                            System.out.println("redisService 异常");
                        }
                    } else {
                        result = false;
                    }
                    break;
                default:
                    return ;
            }
            index += length * 2;
        }
        hashMap.put(devEUI, result);
    }

    //实时数据处理
    public static List<JSONObject> realTimeDataHander(String payload) {
        try {
            JSONObject jsonObject = new JSONObject(payload);
            String devEUI = jsonObject.getString("devEUI");

            //byte[] 转 hex string
            byte[] bytes = decoder.decode(jsonObject.getString("data"));
            StringBuilder buf = new StringBuilder();
            for (byte b : bytes) {
                buf.append(String.format("%02x", new Integer(b & 0xff)));
            }
            System.out.println(buf.toString());

            String date = getNowDate("yyyyMMddHHmmss");

            //分析处理数据
            return realTimeDataAnalysis(buf.toString(), devEUI, date);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
   /* *
    *
    * 功能描述:
    * 返回key-形式
    * @param: [payload]
    * @return: java.util.HashMap<java.lang.String,org.json.JSONObject>
    * @auther: liuyunxing
    * @Description //TODO
    * @date: 2018/7/18 22:56，
    */
    public static HashMap<String, Object> currentDataHander(String payload) {
        try {
            JSONObject jsonObject = new JSONObject(payload);
            String devEUI = jsonObject.getString("devEUI");

            //byte[] 转 hex string
            byte[] bytes = decoder.decode(jsonObject.getString("data"));
            StringBuilder buf = new StringBuilder();
            for (byte b : bytes) {
                buf.append(String.format("%02x", new Integer(b & 0xff)));
            }
            System.out.println(buf.toString());

            String date = getNowDate("yyyyMMddHHmmss");
            date =  formatDate(date);
            //分析处理数据
            return currentDataAnalysis(buf.toString(), devEUI, date);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String formatDate(String date) {


        String hour = "00";
        String min ="00";
        String second = "00";
        if (date.length() >= 14) {
            try {
                hour = date.substring(8, 10);
                min = date.substring(10, 12);
                second = date.substring(12, 14);
            }catch (Exception e){
                System.out.println("字符串转换出现异常");
            }
        }
        return hour + ":"+min+":"+second;
    }

    //实时数据分析,以数组的形式返回数据
    public static List<JSONObject> realTimeDataAnalysis(String hexstr, String devEUI, String date) {
        if (hexstr.length() <= 4 ) {
            System.out.println("数据格式有误！");
            return null;
        }
        //CRC 校验
        String CRCstr = hexstr.substring(hexstr.length() - 4, hexstr.length());
        String forcheck = hexstr.substring(0, hexstr.length() - 4);
        if (CRC16Modbus.checkCRC16(forcheck, CRCstr)) {
            System.out.println("CRC校验成功！");
        } else {
            System.out.println("CRC校验失败！");
            return null;
        }

        //判断是否为传感器数据
        if (forcheck.length() <= 6) {
            throw new IndexOutOfBoundsException();
        }
        String type = forcheck.substring(0, 2);

        if (!type.equals(Instructions.UPLINK_SENSOR_DATA)) {
            return null;
        }

        List<JSONObject> jsonObjectList = new ArrayList<JSONObject>();
        //分析数据
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
                        System.out.println("风速：" + fengsu + "m/s");
                        String choice = "wind";
                        String value = "" + fengsu;
                        RealTimeInfo realTimeInfo = new RealTimeInfo(date, devEUI, typeid, choice, value);
                        jsonObjectList.add(realTimeInfo.toJSONObject());
                    }
                    break;
                case Instructions.SENSOR_GTH:
                    if (length == 6 && !forcheck.substring(index, index + 4).equals("ffff") &&
                            !forcheck.substring(index + 4, index + 8).equals("ffff") &&
                            !forcheck.substring(index + 8, index + 12).equals("ffff")) {
                        float qiti = (float) (Integer.parseInt(forcheck.substring(index, index + 4), 16) / 10.0);
                        float wendu = (float) (Integer.parseInt(forcheck.substring(index + 4, index + 8), 16) / 10.0);
                        float shidu = (float) (Integer.parseInt(forcheck.substring(index + 8, index + 12), 16) / 100.0);
                        System.out.println("气体浓度：" + qiti + "\n温度：" + wendu + "0C\n湿度：" + shidu);

                        RealTimeInfo realTimeInfo_qiti = new RealTimeInfo(date, devEUI, typeid, "gas", String.valueOf(qiti));
                        RealTimeInfo realTimeInfo_wendu = new RealTimeInfo(date, devEUI, typeid, "temperature", String.valueOf(wendu));
                        RealTimeInfo realTimeInfo_shidu = new RealTimeInfo(date, devEUI, typeid, "humidity", String.valueOf(shidu));
                        jsonObjectList.add(realTimeInfo_qiti.toJSONObject());
                        jsonObjectList.add(realTimeInfo_wendu.toJSONObject());
                        jsonObjectList.add(realTimeInfo_shidu.toJSONObject());
                    }
                    break;
                default:
                    return null;
            }
            index += length * 2;
        }
        return jsonObjectList;
    }

    /* *
     *
     * 功能描述: 以key，object的形式向前端返回数据
     *
     * @param: [hexStr, devEui, data]
     * @return: java.util.HashMap<java.lang.String,org.json.JSONObject>
     * @auther: liuyunxing
     * @Description //TODO
     * @date: 2018/7/18 22:39
     */
    public static HashMap<String, Object> currentDataAnalysis(String hexStr, String devEUI, String date) {
        if (hexStr.length() <= 4 ) {
            System.out.println("数据格式有误！");
            return null;
        }
        //CRC 校验
        String CRCstr = hexStr.substring(hexStr.length() - 4, hexStr.length());
        String forcheck = hexStr.substring(0, hexStr.length() - 4);
        if (CRC16Modbus.checkCRC16(forcheck, CRCstr)) {
            System.out.println("CRC校验成功！");
        } else {
            System.out.println("CRC校验失败！");
            return null;
        }

        //判断是否为传感器数据
        if (forcheck.length() <= 6) {
            throw new IndexOutOfBoundsException();
        }
        String type = forcheck.substring(0, 2);

        if (!type.equals(Instructions.UPLINK_SENSOR_DATA)) {
            return null;
        }

        forcheck = forcheck.substring(2, forcheck.length());
       HashMap<String, Object> res = new HashMap();
        //分析数据
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
                        System.out.println("风速：" + fengsu + "m/s");
                        String choice = "wind";
                        String value = "" + fengsu;
                        RealTimeInfo realTimeInfo = new RealTimeInfo(date, devEUI, typeid, choice, value);
                        res.put("wind", realTimeInfo);
                    }
                    break;
                case Instructions.SENSOR_GTH:
                    if (length == 6 && !forcheck.substring(index, index + 4).equals("ffff") &&
                            !forcheck.substring(index + 4, index + 8).equals("ffff") &&
                            !forcheck.substring(index + 8, index + 12).equals("ffff")) {
                        float qiti = (float) (Integer.parseInt(forcheck.substring(index, index + 4), 16) / 10.0);
                        float wendu = (float) (Integer.parseInt(forcheck.substring(index + 4, index + 8), 16) / 10.0);
                        float shidu = (float) (Integer.parseInt(forcheck.substring(index + 8, index + 12), 16) / 100.0);
                        System.out.println("气体浓度：" + qiti + "\n温度：" + wendu + "0C\n湿度：" + shidu);

                        RealTimeInfo realTimeInfo_qiti = new RealTimeInfo(date, devEUI, typeid, "gas", String.valueOf(qiti));
                        RealTimeInfo realTimeInfo_wendu = new RealTimeInfo(date, devEUI, typeid, "temperature", String.valueOf(wendu));
                        RealTimeInfo realTimeInfo_shidu = new RealTimeInfo(date, devEUI, typeid, "humidity", String.valueOf(shidu));
                        res.put("gas", realTimeInfo_qiti);
                        res.put("temperature", realTimeInfo_wendu);
                        res.put("humidity", realTimeInfo_shidu);
                    }
                    break;
                default:
                    return null;
            }
            index += length * 2;
        }
        return res;
    }

    //构造返回的hashMap<String, String>
    public static HashMap<String, String> toHashMap() {
        return null;
    }


    //pattern : yyyyMMddHH   yyyyMMddHHmmsss
    public static String getNowDate(String pattern) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

}
