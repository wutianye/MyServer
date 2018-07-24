package com.example.demo.Utils;

import com.example.demo.Entity.Data;
import com.example.demo.Service.DataService;
import com.example.demo.Service.Impl.RedisServiceImpl;
import com.example.demo.Service.RedisService;
import org.springframework.cache.annotation.Cacheable;

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

    //获取给定日期、某devEUI、typeid的某日数据list
    public static List<Data> getdatalist(DataService dataService, String date, String devEUI, String typeid) {
        List<Data> dataList = new ArrayList<Data>();
        Set<Data> dataSet = new HashSet<Data>();
        String str = "";
        String pattern = date + "*_" + devEUI + "_" + typeid;
        Set<String> stringSet = redisService.getKeysByPattern(pattern);
        //按每天去查，如果这一天的数据少于240 * 24条，则去MySQL中取数据
        if (stringSet.size() < 240 * 24) {
            //当redis中没有全部需要的数据时，去mysql里查询，并存入redis一份
            str = date + "%";
            dataSet.addAll(dataService.findByDateLikeAndDevEUIAndTypeid(str, devEUI, typeid));
            //存redis一份，放线程中处理
            redisHander(dataSet);
        } else {
            for (String key : stringSet) {
                String value = redisService.getValue(key);
                Data data = new Data(key.substring(0, key.indexOf("_")), devEUI, typeid, value);
                dataSet.add(data);
            }
        }
        dataList.addAll(dataSet);
        return dataList;
    }

    //获取给定日期、某devEUI、typeid的某日数据list（一个传感器有多个数据）
    public static List<Data> getdatalistwithoption(DataService dataService, String date, String devEUI, String typeid, String choice) {
        List<Data> dataList = new ArrayList<Data>();
        Set<Data> dataSet = new HashSet<Data>();
        String str = "";
        String pattern = date + "*_" + devEUI + "_" + typeid;
        Set<String> stringSet = redisService.getKeysByPattern(pattern);
        //按每天去查，如果这一天的数据少于240 * 24条，则去MySQL中取数据
        if (stringSet.size() < 240 * 24) {
            //当redis中没有全部需要的数据时，去mysql里查询，并存入redis一份
            str = date + "%";
            dataSet.addAll(dataService.findByDateLikeAndDevEUIAndTypeid(str, devEUI, typeid));
            redisHander(dataSet);
        } else {
            for (String key : stringSet) {
                String value = redisService.getValue(key);
                Data data = new Data(key.substring(0, key.indexOf("_")), devEUI, typeid, value);
                dataSet.add(data);
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

    /* @Cacheable(value="userCache") //缓存,这里没有指定key. 允许缓存*/
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
        Set<String> redisKeysSet = new HashSet<String >();
        try {
            Date startdate = df.parse(date1);
            Date enddate = df.parse(date2);
            List<Date> dateList = getMonthBetweenDate(startdate, enddate);
            int length = dateList.size();//一共多少天
            String dateid1 = date1 + "000000";
            String dateid2 = date2 + "235959";
            for (Date date : dateList) {
                String dateid = df.format(date);
                String pattern = dateid + "*_" + devEUI + "_" + typeid;
                Set<String> stringSet = redisService.getKeysByPattern(pattern);
                redisKeysSet.addAll(stringSet);
            }
            //按总天数去查，如果这些天的数据少于240*24*length条，则去MySQL中取数据
            if (redisKeysSet.size() < 240 * 24 * length) {
                dataSet.addAll(dataService.findByDateBetweenDate1AndDate2AndDevEUIAndTypeid(dateid1, dateid2, devEUI, typeid));
                redisHander(dataSet);
            } else {
                for (String key : redisKeysSet) {
                    String value = redisService.getValue(key);
                    Data data = new Data(key.substring(0, key.indexOf("_")), devEUI, typeid, value);
                    dataSet.add(data);
                }
            }
            dataList.addAll(dataSet);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dataList;
    }

    //获取日期区间内某devEUI、typeid的24时数据list（一个传感器有多个数据）
    public static List<Data> getdatalistwithdaterangewithoption(DataService dataService, String date1, String date2, String devEUI, String typeid, String choice) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        List<Data> dataList = new ArrayList<Data>();
        Set<Data> dataSet = new HashSet<Data>();
        Set<String> redisKeysSet = new HashSet<String >();
        try {
            Date startdate = df.parse(date1);
            Date enddate = df.parse(date2);
            List<Date> dateList = getMonthBetweenDate(startdate, enddate);
            int length = dateList.size();//一共多少天
            String dateid1 = date1 + "000000";
            String dateid2 = date2 + "235959";
            for (Date date : dateList) {
                String dateid = df.format(date);
                String pattern = dateid + "*_" + devEUI + "_" + typeid;
                Set<String> stringSet = redisService.getKeysByPattern(pattern);
                redisKeysSet.addAll(stringSet);
            }
            //按总天数去查，如果这些天的数据少于240*24*length条，则去MySQL中取数据
            if (redisKeysSet.size() < 240 * 24 * length) {
                dataSet.addAll(dataService.findByDateBetweenDate1AndDate2AndDevEUIAndTypeid(dateid1, dateid2, devEUI, typeid));
                redisHander(dataSet);
            } else {
                for (String key : redisKeysSet) {
                    String value = redisService.getValue(key);
                    Data data = new Data(key.substring(0, key.indexOf("_")), devEUI, typeid, value);
                    dataSet.add(data);
                }
            }
            for (Data data : dataSet) {
                data.setValue(handleChoice(choice, data.getValue()));
                dataList.add(data);
            }
        } catch (ParseException e) {
            e.printStackTrace();
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


    //服务器线程，处理mysql查到的数据存redis操作
    public static void redisHander(Set<Data> dataSet) {
        Runnable thread = new Runnable() {
            @Override
            public void run() {
                for (Data data : dataSet) {
                    String key = data.getDate() + "_" + data.getDevEUI() + "_" + data.getTypeid();
                    redisService.setValue(key, data.getValue());
                }
            }
        };
        DataProcess.executor.execute(thread);
    }
}
