package com.example.demo.Service.Impl;

import com.example.demo.Entity.UserDevice;
import com.example.demo.Repository.UserDeviceJpaRepository;
import com.example.demo.Service.DeviceSensorService;
import com.example.demo.Service.SensorTypeService;
import com.example.demo.Service.UserDeviceService;
import com.example.demo.Utils.DeviceSensorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class UserDeviceServiceImpl implements UserDeviceService{

    @Autowired
    private UserDeviceJpaRepository userDeviceJpaRepository;
    @Autowired
    private DeviceSensorService   deviceSensorService;

    @Autowired
    private SensorTypeService  sensorTypeService;

    @Override
    public void insert(UserDevice userDevice) {
        userDeviceJpaRepository.saveAndFlush(userDevice);
    }

    @Override
    public boolean exists(String devEUI) {
        return userDeviceJpaRepository.existsById(devEUI);
    }

    @Override
    public List<UserDevice> findAllByUserid(String userid) {
        return userDeviceJpaRepository.findUserDevicesByUserid(userid);
    }
    /* *
     *
     * 功能描述:
     *
     * @param: [userId]
     * @return: java.util.List
     * @auther: liuyunxing
     * @Description //TODO
     * @date: 2018/7/18 14:15
     */
    @Override
    public HashMap getDSList(String userId) {

        // 首先获得用户的传感器列表
        List<UserDevice> devList = this.findAllByUserid(userId); // 获得列表
        List  desensor = new ArrayList();
        HashMap res = new HashMap();
        if (devList != null) {
            String[]  devArr =  new  String[devList.size()];
            for (int i=0; i < devList.size(); i++){
                HashMap<String , Object> desensorEle = new HashMap<>();
                devArr[i] = devList.get(i).getDevname(); // 获得了传感器名称
                // 去获得传感器列表
                List<HashMap<String,String>> list =
                        DeviceSensorUtil.getSensor(deviceSensorService,sensorTypeService,devList.get(i).getDevEUI() ); // 获得传感器列表
                // 列表
                desensorEle.put("name",devArr[i]); // push名称
                desensorEle.put("value",list.size()); // 数量
                desensor.add(desensorEle);
            }
            res.put("dename", devArr);
            res.put("desensor",desensor); // 数据
        }
        return res;
    }

    @Override
    public void deleteByUserid(String userid) {
        userDeviceJpaRepository.deleteAllByUserid(userid);
        userDeviceJpaRepository.flush();
    }

    @Override
    public void deleteBydevEUI(String devEUI) {
        userDeviceJpaRepository.deleteByDevEUI(devEUI);
    }

}
