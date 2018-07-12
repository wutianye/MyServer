package com.example.demo.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Base64;

public class DataProcess {
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

                //分析并存储数据
                hexStringAnalysis(buf.toString(), devEUI);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    public static void hexStringAnalysis(String hexstr, String devEUI) {
        //CRC 校验
        String CRCstr = hexstr.substring(hexstr.length() - 4, hexstr.length());
        String forcheck = hexstr.substring(0, hexstr.length() - 4);
        if (CRC16Modbus.checkCRC16(forcheck, CRCstr)) {
            System.out.println("CRC校验成功！");
        } else {
            System.out.println("CRC校验失败！");
            return ;
        }

        //分析数据

        for (int index = 0; index < forcheck.length(); ) {
            String typeid = forcheck.substring(index, index + 2);
            int length = Integer.parseInt(forcheck.substring(index + 2, index + 4), 16);
            index += 4;
            switch (typeid) {
                case "01":
                    if (length == 2) {
                        float data = (float) (Integer.parseInt(forcheck.substring(index, index + 4), 16) / 100.0);
                        System.out.println("风速：" + data + "m/s");
                        //存数据


                    }
                    break;
                case "02":
                    if (length == 6){
                        float qiti = (float) (Integer.parseInt(forcheck.substring(index, index + 4), 16) / 10.0);
                        float wendu = (float) (Integer.parseInt(forcheck.substring(index + 4, index + 8), 16) / 10.0);
                        float shidu = (float) (Integer.parseInt(forcheck.substring(index + 8, index + 12), 16) / 100.0);
                        System.out.println("风速：" + qiti + "m/s\n温度：" + wendu + "0C\n湿度：" + shidu);
                        //存数据


                    }
                    break;
                default:
                    return ;
            }
            index += length;
        }

    }
}
