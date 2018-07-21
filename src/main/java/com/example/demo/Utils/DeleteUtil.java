package com.example.demo.Utils;


import com.example.demo.Entity.UserToken;
import com.example.demo.Service.*;
import com.example.demo.Service.Impl.*;

public class DeleteUtil {

    private static UserService userService = SpringBeanFactoryUtil.getBean(UserServiceImpl.class);
    private static UserTokenService userTokenService = SpringBeanFactoryUtil.getBean(UserTokenServiceImpl.class);
    private static UserDeviceService userDeviceService = SpringBeanFactoryUtil.getBean(UserDeviceServiceImpl.class);
    private static DeviceSensorService deviceSensorService = SpringBeanFactoryUtil.getBean(DeviceSensorServiceImpl.class);
    private static DeviceRelayService deviceRelayService = SpringBeanFactoryUtil.getBean(DeviceRelayServiceImpl.class);
    private static DataService dataService = SpringBeanFactoryUtil.getBean(DataServiceImpl.class);

    //删除给定devEUI、typeid有关的数据
    public static TMessage deleteData() {
        return null;
    }

    //用户或管理员删除指定devEUI下指定的传感器
    public static TMessage deleteSensor(String devEUI, String typeid) {
        return null;
    }


}
