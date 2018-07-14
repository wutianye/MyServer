package com.example.demo.Utils;

import com.example.demo.Entity.Data;
import com.example.demo.Service.DataService;
import com.example.demo.Service.Impl.DataServiceImpl;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;



public class DataProcess {

    private static DataService dataService = SpringBeanFactoryUtil.getBean(DataServiceImpl.class);

    public static String currentTime = "";

    public static HashMap<String, Boolean> hashMap = new HashMap<String, Boolean>();

    static final Base64.Decoder decoder = Base64.getDecoder();
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

                String date = getNowDate();
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
        //CRC 校验
        String CRCstr = hexstr.substring(hexstr.length() - 4, hexstr.length());
        String forcheck = hexstr.substring(0, hexstr.length() - 4);
        if (CRC16Modbus.checkCRC16(forcheck, CRCstr)) {
            System.out.println("CRC校验成功！");
        } else {
            System.out.println("CRC校验失败！");
            return ;
        }

        boolean result = true;
        //分析数据
        for (int index = 0; index < forcheck.length(); ) {
            String typeid = forcheck.substring(index, index + 2);
            int length = Integer.parseInt(forcheck.substring(index + 2, index + 4), 16);
            index += 4;
            switch (typeid) {
                case "01":
                    if (length == 2 && !forcheck.substring(index, index+4).equals("ffff")) {
                        float fengsu = (float) (Integer.parseInt(forcheck.substring(index, index + 4), 16) / 100.0);
                        System.out.println("风速：" + fengsu + "m/s");
                        //存数据
                        String value = "" + fengsu;
                        Data data = new Data(date, devEUI, typeid, value);
                        try{
                            dataService.insert(data);
                        }catch (Exception e){
                          //  e.printStackTrace();
                            System.out.println("dataService 异常");
                        }

                    } else {
                        result = false;
                    }
                    break;
                case "02":
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
                        try{
                            dataService.insert(data);
                        }catch (Exception e){
                          //  e.printStackTrace();
                            System.out.println("dataService 异常");
                        }
                    } else {
                        result = false;
                    }
                    break;
                default:
                    return ;
            }
            index += length;
        }
        hashMap.put(devEUI, result);
    }

    public static String getNowDate() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhh");
        return sdf.format(date);
    }
}
