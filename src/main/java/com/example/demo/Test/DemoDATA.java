package com.example.demo.Test;
import com.alibaba.fastjson.JSONObject;


import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: yunxi
 * Date: 2018/7/18
 * Time: 19:10
 * Description: No Description
 */
public class DemoDATA {

    private static  double getRandomData(int left , int right){
        return Math.random()*left + (right - left);
    }
    private static String getNowTime(){
        Calendar calendar = Calendar.getInstance();
        int hour =  calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        return hour+":"+minute+":"+second;
    }
    public  static  String getDemoDATA()  {

        String nowTime = getNowTime();
        Data wind = new Data(getRandomData(100,200),nowTime);
        Data humidity = new Data(getRandomData(20,40), nowTime);
        Data  gas = new Data(getRandomData(5,10), nowTime);
        Data  temperature =  new Data(getRandomData(25,40), nowTime);
        JSONDATA jsondata = new JSONDATA(wind, temperature, humidity, gas);
        return JSONObject.toJSONString(jsondata);
    }

    public static void main(String[] args) {
        System.out.println(DemoDATA.getDemoDATA());
    }
}
