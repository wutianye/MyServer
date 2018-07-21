package com.example.demo.Utils;


import com.example.demo.Entity.UserToken;
import com.example.demo.Service.Impl.UserDeviceServiceImpl;
import com.example.demo.Service.Impl.UserServiceImpl;
import com.example.demo.Service.Impl.UserTokenServiceImpl;
import com.example.demo.Service.UserDeviceService;
import com.example.demo.Service.UserService;
import com.example.demo.Service.UserTokenService;

public class DeleteUtil {

    private static UserService userService = SpringBeanFactoryUtil.getBean(UserServiceImpl.class);
    private static UserTokenService userTokenService = SpringBeanFactoryUtil.getBean(UserTokenServiceImpl.class);
    private static UserDeviceService userDeviceService = SpringBeanFactoryUtil.getBean(UserDeviceServiceImpl.class);


}
