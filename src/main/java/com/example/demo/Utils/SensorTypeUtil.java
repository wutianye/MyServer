package com.example.demo.Utils;

import com.example.demo.Entity.SensorType;
import com.example.demo.Service.SensorTypeService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SensorTypeUtil {

    public static List<HashMap<String,String>> getall(SensorTypeService sensorTypeService) {
        List<HashMap<String, String>> hashMapList = new ArrayList<HashMap<String, String>>();
        List<SensorType> sensorTypeList = sensorTypeService.findAll();
        for (SensorType sensorType : sensorTypeList) {
            hashMapList.add(toHashmap(sensorType));
        }
        return hashMapList;
    }

    //sensortype to hashmap
    public static HashMap<String, String> toHashmap(SensorType sensorType) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("typeid", sensorType.getTypeid());
        hashMap.put("typename", sensorType.getTypename());
        return hashMap;
    }

}
