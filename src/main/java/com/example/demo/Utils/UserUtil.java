package com.example.demo.Utils;

import com.example.demo.Entity.User;
import com.example.demo.Entity.UserToken;
import com.example.demo.Service.UserService;
import com.example.demo.Service.UserTokenService;


public class UserUtil {

    public static Info register(UserService userService, User user) {
        Info info = new Info();
        if (userService.exists(user.getUserid())) {
            info.setResult(false);
            info.setInfo("该账号已被注册！");
            return info;
        }
        userService.insert(user);
        if (userService.exists(user.getUserid())) {
            info.setResult(true);
            info.setInfo("注册成功！");
        } else {
            info.setResult(false);
            info.setInfo("注册失败！未知错误");
        }
        return info;
    }

    public static Info weblogin(UserService userService, UserTokenService userTokenService, String userid, String password) {
        Info info = new Info();
        if (!userService.exists(userid)) {
            info.setResult(false);
            info.setInfo("用户名不存在或密码错误！");
            return info;
        }
        User user = userService.findByUserid(userid);
        if (user.getPassword().equals(password)) {
            info.setResult(true);
            try {
                info.setToken(JwtToken.createToken(userid));
                if (userTokenService.exists(userid)) {
                    UserToken userToken = userTokenService.findByUserid(userid);
                    userToken.setWebtoken(info.getToken());
                    userTokenService.insert(userToken);
                } else {
                    UserToken userToken = new UserToken(userid, info.getToken(), null);
                    userTokenService.insert(userToken);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            info.setInfo(user.getFlag());
        } else {
            info.setResult(false);
            info.setInfo("用户名不存在或密码错误！");
        }
        return info;
    }

    public static Info applogin(UserService userService, UserTokenService userTokenService, String userid, String password) {
        Info info = new Info();
        if (!userService.exists(userid)) {
            info.setResult(false);
            info.setInfo("用户名不存在或密码错误！");
            return info;
        }
        User user = userService.findByUserid(userid);
        if (user.getPassword().equals(password) && user.getFlag().equals("common")) {
            info.setResult(true);
            try {
                info.setToken(JwtToken.createToken(userid));
                if (userTokenService.exists(userid)) {
                    UserToken userToken = userTokenService.findByUserid(userid);
                    userToken.setApptoken(info.getToken());
                    userTokenService.insert(userToken);
                } else {
                    UserToken userToken = new UserToken(userid, null, info.getToken());
                    userTokenService.insert(userToken);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            info.setInfo("登陆成功！");
        } else {
            info.setResult(false);
            info.setInfo("用户名不存在或密码错误！");
        }
        return info;
    }

}
