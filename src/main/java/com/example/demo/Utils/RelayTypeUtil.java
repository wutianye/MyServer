package com.example.demo.Utils;

import com.example.demo.Entity.RelayType;
import com.example.demo.Service.RelayTypeService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RelayTypeUtil {
    public static List<HashMap<String,String>> getall(RelayTypeService relayTypeService) {
        List<HashMap<String, String>> hashMapList = new ArrayList<HashMap<String, String>>();
        List<RelayType> relayTypeList = relayTypeService.findAll();
        for (RelayType relayType : relayTypeList) {
            hashMapList.add(toHashmap(relayType));
        }
        return hashMapList;
    }

    //relaytype to hashmap
    public static HashMap<String, String> toHashmap(RelayType relayType) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("relayType", relayType.getRelayType());
        hashMap.put("relayName", relayType.getRelayName());
        return hashMap;
    }
}
