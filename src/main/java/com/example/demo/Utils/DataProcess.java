package com.example.demo.Utils;

import com.alibaba.fastjson.JSON;
import com.example.demo.Entity.Data;
import com.example.demo.Entity.DeviceSensor;
import com.example.demo.Entity.UserDevice;
import com.example.demo.Service.DataService;
import com.example.demo.Service.DeviceSensorService;
import com.example.demo.Service.Impl.DataServiceImpl;
import com.example.demo.Service.Impl.DeviceSensorServiceImpl;
import com.example.demo.Service.Impl.RedisServiceImpl;
import com.example.demo.Service.Impl.UserDeviceServiceImpl;
import com.example.demo.Service.RedisService;
import com.example.demo.Service.UserDeviceService;
import io.swagger.models.auth.In;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;


public class DataProcess {

    private static DataService dataService = SpringBeanFactoryUtil.getBean(DataServiceImpl.class);
    private static RedisService redisService = SpringBeanFactoryUtil.getBean(RedisServiceImpl.class);
    private static DeviceSensorService deviceSensorService = SpringBeanFactoryUtil.getBean(DeviceSensorServiceImpl.class);
    private static UserDeviceService userDeviceService = SpringBeanFactoryUtil.getBean(UserDeviceServiceImpl.class);

    //定义线程池
    public static ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    static {
        //配置核心线程数
        executor.setCorePoolSize(5);
        //配置最大线程数
        executor.setMaxPoolSize(5);
        //配置队列大小
        executor.setQueueCapacity(99999);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("async-service-");

        // rejection-policy：当pool已经达到max size的时候，如何处理新任务
        // CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //执行初始化
        executor.initialize();
    }

//    public static String currentTime = "";

//    public static HashMap<String, Boolean> hashMap = new HashMap<String, Boolean>();

    static final Base64.Decoder decoder = Base64.getDecoder();
    static final Base64.Encoder encoder = Base64.getEncoder();

    //接收数据及请求配置
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

    //接收数据及请求配置处理
    public static void hexStringAnalysis(String hexstr, String devEUI) {
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

        String type = forcheck.substring(0, 2);
        System.out.println("type:" + type);
        if (type.equals(Instructions.UPLINK_CONFIGURE)) {
            Runnable thread = new Runnable() {
                @Override
                public void run() {
                    System.out.println("线程：");
                    downLinkConfigHander(devEUI);
                    return;
                }
            };
            executor.execute(thread);

            return;
        }

        if (!type.equals(Instructions.UPLINK_SENSOR_DATA)) {
            return;
        }
        forcheck = forcheck.substring(2, forcheck.length());


        //Android端实时数据存储
        RealTimeUtil.saveRealTimeData(devEUI, forcheck);


        //对  同一设备同一传感器类型的数据  隔1h存储一次
//        String date = getNowDate("yyyyMMddHH");
//        if (!date.equals(currentTime)) {
//            currentTime = date;
//            hashMap.clear();
//        }
//        if (hashMap.containsKey(devEUI)){
//            if (hashMap.get(devEUI)) {
//                return;
//            }
//        } else {
//            hashMap.put(devEUI, false);
//        }


        String date = getNowDate("yyyyMMddHHmmss");
        List<Data> dataList = new ArrayList<Data>();
//        boolean result = true;
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
//                        存数据
                        String value = "" + fengsu;
                        Data data = new Data(date, devEUI, typeid, value);
//                        try {
//                            dataService.insert(data);
//                        } catch (Exception e) {
//                            System.out.println("dataService 异常");
//                        }
                        dataList.add(data);

//                        存入redis数据库
                        String key = date + "_" + devEUI + "_" + typeid;
                        try {
                            redisService.setValue(key, value);
                        } catch (Exception e) {
                            System.out.println("redisService 异常");
                        }

                    }
//                    else {
//                        result = false;
//                    }
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
//                        try {
//                            dataService.insert(data);
//                        } catch (Exception e) {
//                            System.out.println("dataService 异常");
//                        }
                        dataList.add(data);

                        //存入redis数据库
                        String key = date + "_" + devEUI + "_" + typeid;
                        try {
                            redisService.setValue(key, value);
                        } catch (Exception e) {
                            System.out.println("redisService 异常");
                        }
                    }
//                    else {
//                        result = false;
//                    }
                    break;
                case Instructions.SENSOR_GPS:
                    if (length == 26) {
                        String GPSstr = new String(CRC16Modbus.HexString2Bytes(forcheck.substring(index, index + length * 2)));
                        System.out.println("GPS:" + GPSstr);
                        String value = GPSHander(GPSstr);
                        if (value == null) {
                            break;
                        }
                        System.out.println("GPSValue: " + value);
                        //存数据
                        Data data = new Data(date, devEUI, typeid, value);
//                        try {
//                            dataService.insert(data);
//                        } catch (Exception e) {
//                            System.out.println("dataService 异常");
//                        }
                        dataList.add(data);

                        //存入redis数据库
                        String key = date + "_" + devEUI + "_" + typeid;
                        try {
                            redisService.setValue(key, value);
                        } catch (Exception e) {
                            System.out.println("redisService 异常");
                        }
                    }
                    break;
                default:
                    return ;
            }
            index += length * 2;
        }
        try {
            dataService.insertList(dataList);
        } catch (Exception e) {
            System.out.println("dataService 异常");
        }
        dataList.clear();
//        hashMap.put(devEUI, result);
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
            System.out.println("json异常");
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

        forcheck = forcheck.substring(2, forcheck.length());
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
                case Instructions.SENSOR_GPS:
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
                case Instructions.SENSOR_GPS:
                    if (length == 26) {
                        String GPSstr = new String(CRC16Modbus.HexString2Bytes(forcheck.substring(index, index + length * 2)));
                        String value = GPSHander(GPSstr);
                        if (value == null) {
                            break;
                        }

                        RealTimeInfo realTimeInfo_latitude = new RealTimeInfo(date, devEUI, typeid, "latitude", value.substring(0, value.indexOf("_")));
                        RealTimeInfo realTimeInfo_longitude = new RealTimeInfo(date, devEUI, typeid, "longitude", value.substring(value.indexOf("_") + 1, value.length()));
                        res.put("latitude", realTimeInfo_latitude);
                        res.put("longitude", realTimeInfo_longitude);
                    }
                    break;
                default:
                    break;
            }
            index += length * 2;
        }
        return res;
    }

    //构造返回的hashMap<String, String>
 /*   public static HashMap<String, String> toHashMap() {
        return null;*/

    //ack处理
    public static boolean ackHander(String payload) {
        try {
            JSONObject jsonObject = new JSONObject(payload);

            //byte[] 转 hex string
            byte[] bytes = decoder.decode(jsonObject.getString("data"));
            StringBuilder buf = new StringBuilder();
            for (byte b : bytes) {
                buf.append(String.format("%02x", new Integer(b & 0xff)));
            }
            System.out.println(buf.toString());
            return ackAnalysis(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    //ack结果分析
    public static boolean ackAnalysis(String hexstr) {
        if (hexstr.length() <= 4 ) {
            System.out.println("数据格式有误！");
            return false;
        }
        //CRC 校验
        String CRCstr = hexstr.substring(hexstr.length() - 4, hexstr.length());
        String forcheck = hexstr.substring(0, hexstr.length() - 4);
        if (CRC16Modbus.checkCRC16(forcheck, CRCstr)) {
            System.out.println("CRC校验成功！");
        } else {
            System.out.println("CRC校验失败！");
            return false;
        }
        //判断字段长度,ack字段e1开头，后面跟结果00或01
        if (forcheck.length() != 4) {
            return false;
        }
        String type = forcheck.substring(0, 2);

        //判断是否为ACK
        if (!type.equals(Instructions.UPLINK_ACK)) {
            return false;
        }
        String result = forcheck.substring(2, forcheck.length());
        if (result.equals("01")) {
            return true;
        }
        return false;

    }

    //请求下发配置处理，下发设备配置
    public static Info downLinkConfigHander(String devEUI) {
        String topic = "application/2/device/" + devEUI + "/tx";
        List<DeviceSensor> deviceSensorList = deviceSensorService.findBydevEUI(devEUI);
        //构造下发的配置指令
        String instruction = makeConfigureInstruction(deviceSensorList, devEUI);
        if (instruction == null) {
            return new Info(false, "构造指令失败！");
        }
        String instructionCRC = MQTTUtil.makeInstructions(instruction);
        if (instructionCRC == null) {
            System.out.println("构造指令失败！");
            return new Info(false, "构造指令失败！");
        }
        Info info = new Info();
        info.setResult(false);
        System.out.println("下发配置...");
        while (!info.isResult()) {
            info = MQTTUtil.publish(topic, MQTTUtil.makeData(instructionCRC), "ack");
            System.out.println("info:" + info);
        }
        System.out.println("下发配置成功！");
        return info;
    }

    //继电器状态处理
    public static String rstateHander(String payload) {
        try {
            JSONObject jsonObject = new JSONObject(payload);

            //byte[] 转 hex string
            byte[] bytes = decoder.decode(jsonObject.getString("data"));
            StringBuilder buf = new StringBuilder();
            for (byte b : bytes) {
                buf.append(String.format("%02x", new Integer(b & 0xff)));
            }
            System.out.println(buf.toString());
            return rstateAnalysis(buf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    //继电器状态结果分析
    public static String rstateAnalysis(String hexstr) {
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
        //判断字段长度,rstate字段e3开头，后面跟结果如ff
        if (forcheck.length() != 4) {
            return null;
        }
        String type = forcheck.substring(0, 2);

        //判断是否为rstate
        if (!type.equals(Instructions.UPLINK_RELAY_STATE)) {
            return null;
        }
        String result = forcheck.substring(2, forcheck.length());
        System.out.println("result :" + result);
        //hexstr转二进制str，如ff转11111111
        return hexString2binaryString(result);
    }

    //构造下发配置指令
    public static String makeConfigureInstruction(List<DeviceSensor> deviceSensorList, String devEUI) {
        String instruction = Instructions.DOWNLINK_SENSOR_CONFIGURE;
        //添加用户自定义的上传速率字段，多少秒一次
        UserDevice userDevice = userDeviceService.findBydevEUI(devEUI);
        String hexstr = "";
        if (userDevice.getFrequency() < 16) {
            hexstr = "0" + Integer.toHexString(userDevice.getFrequency());
        } else if (userDevice.getFrequency() > 60){
            return null;
        } else  {
            hexstr = Integer.toHexString(userDevice.getFrequency());
        }
        instruction = instruction + hexstr;

        int count = 0;
        for (DeviceSensor deviceSensor : deviceSensorList) {
            if (deviceSensor.getTypeid().equals(Instructions.SENSOR_WIND) && deviceSensor.getState().equals("1")) {
                count++;
                instruction = instruction + Instructions.CONFIGURE_OPEN_WIND;
            }
            if (deviceSensor.getTypeid().equals(Instructions.SENSOR_GTH) && deviceSensor.getState().equals("1")) {
                count++;
                instruction = instruction + Instructions.CONFIGURE_OPEN_GTH;
            }
        }
        if (count == 0) {
            instruction = instruction + Instructions.CONFIGURE_CLOSEALL;
        }
        return instruction;
    }

    //pattern : yyyyMMddHH   yyyyMMddHHmmsss
    public static String getNowDate(String pattern) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    //hexstr 转 二进制str
    public static String hexString2binaryString(String hexString) {
        if (StringUtil.isEmpty(hexString)) {
            return null;
        }
        String binaryString = "";
        for (int i = 0; i < hexString.length(); i++) {
            //截取hexStr的一位
            String hex = hexString.substring(i, i + 1);
            //通过toBinaryString将十六进制转为二进制
            String binary = Integer.toBinaryString(Integer.parseInt(hex, 16));
            //因为高位0会被舍弃，先补上4个0
            String tmp = "0000" + binary;
            //取最后4位，将多补的0去掉
            binaryString += tmp.substring(tmp.length() - 4);
        }
        return binaryString;
    }

    //处理GPS数据，返回 纬度_经度 值，如：118.666_31.5233, -118.22_31.222
    public static String GPSHander(String GPSstr) {
        try {
            String latitudeValue = GPSstr.substring(0, GPSstr.indexOf(","));
            String latitudeDirection = GPSstr.substring(GPSstr.indexOf(",") + 1, GPSstr.indexOf(",", GPSstr.indexOf(",") + 1));
            String substr = GPSstr.substring(GPSstr.indexOf(",", GPSstr.indexOf(",") + 1) + 1 ,GPSstr.length());
            String longtitudeValue = substr.substring(0, substr.indexOf(","));
            String longtitudeDirection = substr.substring(substr.indexOf(",") + 1, substr.length());

            String returnvalue = "";
            String dir = latitudeDirection.equals("S") ? "-":"";
            int i = Integer.parseInt(latitudeValue.substring(0, latitudeValue.indexOf(".")));
            int z = Integer.parseInt(latitudeValue.substring(latitudeValue.indexOf(".") + 1), latitudeValue.length());
            int x = i / 100;
            int y = i % 100;
            float f = (float) (x + y / 60.0 + z / 6000000.0);
            returnvalue = dir + returnvalue + f;

            dir = longtitudeDirection.equals("W") ? "-":"";
            i = Integer.parseInt(longtitudeValue.substring(0, longtitudeValue.indexOf(".")));
            z = Integer.parseInt(longtitudeValue.substring(longtitudeValue.indexOf(".") + 1), longtitudeValue.length());
            x = i / 100;
            y = i % 100;
            f = (float) (x + y / 60.0 + z / 6000000.0);
            returnvalue = dir + returnvalue + "_"+ f;

            return returnvalue;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("GPS数据格式错误！");
        }
        return null;
    }

}
