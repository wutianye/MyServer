package com.example.demo.Utils;

import com.example.demo.Entity.Data;
import com.example.demo.Service.DataService;
import com.example.demo.Service.Impl.RedisServiceImpl;
import com.example.demo.Service.RedisService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DataUtil {

    private static RedisService redisService = SpringBeanFactoryUtil.getBean(RedisServiceImpl.class);

    public static List<HashMap<String, String>> getdatabydate(DataService dataService, String date, String devEUI, String typeid, String choice) {
        List<Data> dataList;
        switch (typeid) {
            case "01":
                dataList = getdatalist(dataService, date, devEUI, typeid);
                break;
            case "02":
                dataList = getdatalistwithoption(dataService, date, devEUI, typeid, choice);
                break;
            default:
                return null;
        }
        return toHashlist(dataList);
    }

    //获取给定日期、某devEUI、typeid的24时数据list
    public static List<Data> getdatalist(DataService dataService, String date, String devEUI, String typeid) {
        List<Data> dataList = new ArrayList<Data>();
        Set<Data> dataSet = new HashSet<Data>();
        int i;
        boolean flag = true;
        String str = "";
        for (i = 0; i < 24 && flag; i++) {
            if (i <= 9) {
                str = date + "0" + i;
            } else {
                str = date + i;
            }
            String key = str + "_" + devEUI + "_" + typeid;
            //先去redis中查找数据，如果没有再去MySQL中查询
            if (redisService.hasKey(key)) {
                String value = redisService.getValue(key);
                Data data = new Data(date, devEUI, typeid, value);
                dataSet.add(data);
            } else {
                //当redis中没有全部需要的数据时，去mysql里查询，并存入redis一份
                flag = false;
                str = "%" + date + "%";
                dataSet.addAll(dataService.findByDateLikeAndDevEUIAndTypeid(str, devEUI, typeid));
                for (Data data : dataSet) {
                    key = data.getDate() + "_" + data.getDevEUI() + "_" + data.getTypeid();
                    redisService.setValue(key, data.getValue());
                }
            }
        }
        dataList.addAll(dataSet);
        return dataList;
    }

    //获取给定日期、某devEUI、typeid的24时数据list（一个传感器有多个数据）
    public static List<Data> getdatalistwithoption(DataService dataService, String date, String devEUI, String typeid, String choice) {
        List<Data> dataList = new ArrayList<Data>();
        Set<Data> dataSet = new HashSet<Data>();
        int i;
        String str = "";
        for (i = 0; i < 24 ; i++) {
            if (i <= 9) {
                str = date + "0" + i;
            } else {
                str = date + i;
            }
            //先去redis中查找数据，如果没有再去MySQL中查询
            String key = str + "_" + devEUI + "_" + typeid;
            if (redisService.hasKey(key)) {
                String value = redisService.getValue(key);
                Data data = new Data(str, devEUI, typeid, value);
                dataSet.add(data);
            } else {
                //当redis中没有全部需要的数据时，去mysql里查询，并存入redis一份
                str = "%" + date + "%";
                dataSet.addAll(dataService.findByDateLikeAndDevEUIAndTypeid(str, devEUI, typeid));
                for (Data data : dataSet) {
                    key = data.getDate() + "_" + data.getDevEUI() + "_" + data.getTypeid();
                    redisService.setValue(key, data.getValue());
                    data.setValue(handleChoice(choice, data.getValue()));
                    dataList.add(data);
                }
                return dataList;
            }
        }
        for (Data data : dataSet) {
            data.setValue(handleChoice(choice, data.getValue()));
            dataList.add(data);
        }
        return dataList;
    }

    //处理choice，返回对应value
    public static String handleChoice(String choice, String value) {
        switch (choice) {
            case "qiti":
                return value.substring(0, value.indexOf("_"));
            case "wendu":
                return value.substring(value.indexOf("_") + 1, value.indexOf("_", value.indexOf("_") + 1));
            case "shidu":
                return value.substring(value.indexOf("_",value.indexOf("_") + 1) + 1, value.length());
            default:
                return null;
        }
    }


    //list<Data> to list<hashmap<String,String>>
    public static List<HashMap<String, String>> toHashlist(List<Data> dataList) {
        List<HashMap<String, String>> hashMapList = new ArrayList<HashMap<String, String>>();
        for (Data data : dataList) {
            HashMap<String, String> hashMap = new HashMap<String, String>();
            hashMap.put("date", data.getDate());
            hashMap.put("value", data.getValue());
            hashMapList.add(hashMap);
        }
        return hashMapList;
    }

    //给定日期区间、devEUI、typeid的所有数据列表
    public static List<HashMap<String, String>> getdatafromdatetodate(DataService dataService, String date1, String date2, String devEUI, String typeid, String choice) {
        List<Data> dataList;
        switch (typeid) {
            case "01":
                dataList = getdatalistwithdaterange(dataService, date1, date2, devEUI, typeid);
                break;
            case "02":
                dataList = getdatalistwithdaterangewithoption(dataService, date1, date2, devEUI, typeid, choice);
                break;
            default:
                return null;
        }
        return toHashlist(dataList);
    }

    //获取日期区间内某devEUI、typeid的24时数据list
    public static List<Data> getdatalistwithdaterange(DataService dataService, String date1, String date2, String devEUI, String typeid) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        List<Data> dataList = new ArrayList<Data>();
        Set<Data> dataSet = new HashSet<Data>();
        int i;
        String str = "";
        try {
            boolean flag = true;
            Date startdate = df.parse(date1);
            Date enddate = df.parse(date2);
            List<Date> dateList = getMonthBetweenDate(startdate, enddate);
            for (Date date : dateList) {
                String dateid = df.format(date);
                for (i = 0; i < 24; i++) {
                    if (i <= 9) {
                        str = dateid + "0" + i;
                    } else {
                        str = dateid + "" + i;
                    }
                    String key = str + "_" + devEUI + "_" + typeid;
                    //先去redis中查找数据，如果没有再去MySQL中查询
                    if (redisService.hasKey(key)) {
                        String value = redisService.getValue(key);
                        Data data = new Data(str, devEUI, typeid, value);
                        dataSet.add(data);
                    } else {
                        //当redis中没有全部需要的数据时，去mysql里查询，并存入redis一份
                        dataSet.addAll(dataService.findByDateBetweenDate1AndDate2AndDevEUIAndTypeid(str, date2 + "23", devEUI, typeid));
                        for (Data data : dataSet) {
                            key = data.getDate() + "_" + data.getDevEUI() + "_" + data.getTypeid();
                            redisService.setValue(key, data.getValue());
                        }
                        flag = false;
                        break;
                    }
                }
                if (!flag) {
                    break;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        dataList.addAll(dataSet);
        return dataList;
    }

    //获取日期区间内某devEUI、typeid的24时数据list（一个传感器有多个数据）
    public static List<Data> getdatalistwithdaterangewithoption(DataService dataService, String date1, String date2, String devEUI, String typeid, String choice) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        List<Data> dataList = new ArrayList<Data>();
        Set<Data> dataSet = new HashSet<Data>();
        int i;
        String str = "";
        try {
            Date startdate = df.parse(date1);
            Date enddate = df.parse(date2);
            List<Date> dateList = getMonthBetweenDate(startdate, enddate);
            for (Date date : dateList) {
                String dateid = df.format(date);
                for (i = 0; i < 24; i++) {
                    if (i <= 9) {
                        str = dateid + "0" + i;
                    } else {
                        str = dateid + "" + i;
                    }
                    String key = str + "_" + devEUI + "_" + typeid;
                    //先去redis中查找数据，如果没有再去MySQL中查询
                    if (redisService.hasKey(key)) {
                        String value = redisService.getValue(key);
                        Data data = new Data(str, devEUI, typeid, value);
                        dataSet.add(data);
                    } else {
                        //当redis中没有全部需要的数据时，去mysql里查询，并存入redis一份
                        dataSet.addAll(dataService.findByDateBetweenDate1AndDate2AndDevEUIAndTypeid(str, date2 + "23", devEUI, typeid));
                        for (Data data : dataSet) {
                            key = data.getDate() + "_" + data.getDevEUI() + "_" + data.getTypeid();
                            redisService.setValue(key, data.getValue());
                            data.setValue(handleChoice(choice, data.getValue()));
                            dataList.add(data);
                        }
                        return dataList;
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        for (Data data : dataSet) {
            data.setValue(handleChoice(choice, data.getValue()));
            dataList.add(data);
        }
        return dataList;
    }

    //两日期之间的所有日期list
    public static List<Date> getMonthBetweenDate(Date beginDate, Date endDate) {
        List lDate = new ArrayList();
        lDate.add(beginDate);//把开始时间加入集合
        if (beginDate.getTime() == endDate.getTime()) {
            return lDate;
        }

        Calendar cal = Calendar.getInstance();
        //使用给定的 Date 设置此 Calendar 的时间
        cal.setTime(beginDate);
        boolean bContinue = true;
        while (bContinue) {
            //根据日历的规则，为给定的日历字段添加或减去指定的时间量
            cal.add(Calendar.DAY_OF_MONTH, 1);
            // 测试此日期是否在指定日期之后
            if (endDate.after(cal.getTime())) {
                lDate.add(cal.getTime());
            } else {
                break;
            }
        }
        lDate.add(endDate);//把结束时间加入集合
        return lDate;
    }

}
