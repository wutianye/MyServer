package com.example.demo.Service.Impl;

import com.example.demo.Entity.User;
import com.example.demo.Repository.UserJpaRepository;
import com.example.demo.Service.UserService;
import com.example.demo.Utils.StringUtil;
import com.example.demo.Utils.TMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Max;
import java.util.HashMap;
import java.util.List;

@Service
public class UserServiceImpl implements UserService{


    private  final String[] roles = {"admin","common","editor","visitor"};
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private UserDeviceServiceImpl userDeviceService; // 根据user查看用户所拥有的传感器
//    private User

    @Override
    public void insert(User user) {
        userJpaRepository.saveAndFlush(user);
    }

    @Override
    public boolean exists(String userid) {
        return userJpaRepository.existsById(userid);
    }

    @Override
    public User findByUserid(String userid) {
        return userJpaRepository.findUserByUserid(userid);
    }

    @Override
    public HashMap getAllUserInfo(String userId) {

        List<User> userList = userJpaRepository.findAll();// 找到所有的user
        HashMap res = new HashMap();
        res.put("userNum", userList.size()); // 获得用户的列表;
        userList.forEach((user -> {
            user.setPassword("no privilige!");
        }));
        res.put("userInfo",userList);
        return res;
    }

    // 修改密码, 用于管理员修改密码
    @Override
    public TMessage modifyPassword(String userId ,String modUserId, String password) {

        User user = userJpaRepository.findUserByUserid(userId); // 查找user
        if (user == null) return new TMessage(TMessage.CODE_FAILURE, "token过期，请重新登陆再次请求");
        if (!user.getFlag().equals("admin")) return  new TMessage(TMessage.CODE_FAILURE, "您没有权限修改别人的密码");
        User modUser = userJpaRepository.findUserByUserid(modUserId);
        if (modUser == null) return new TMessage(TMessage.CODE_FAILURE, "您要修改密码的用户不存在");
        if (modUser.getFlag().equals("admin")) return  new TMessage(TMessage.CODE_FAILURE, "您不可以修改管理员的密码");
        modUser.setPassword(password); // 修改密码
        userJpaRepository.saveAndFlush(modUser); // 修改密码成功
        return new TMessage(TMessage.CODE_SUCCESS,"修改密码成功");
    }

    @Override
    public TMessage modifyUserRole(String userId, String modUserId, String role) {
        User user = userJpaRepository.findUserByUserid(userId); // 查找user
        if (user == null) return new TMessage(TMessage.CODE_FAILURE, "token过期，请重新登陆再次请求");
        if (!user.getFlag().equals("admin")) return  new TMessage(TMessage.CODE_FAILURE, "您没有权限修改别人的角色");
        User modUser = userJpaRepository.findUserByUserid(modUserId);
        if (modUser == null) return new TMessage(TMessage.CODE_FAILURE, "您要修改密码的用户不存在");
        if (modUser.getFlag().equals("admin")) return  new TMessage(TMessage.CODE_FAILURE, "您不可以修改管理员角色");
        if (!StringUtil.judgValidRole(roles, role)) return  new TMessage(TMessage.CODE_FAILURE, "请选择正确的身份");
        modUser.setFlag(role);
        userJpaRepository.saveAndFlush(modUser);// 修改用户身份
        return  new TMessage(TMessage.CODE_SUCCESS, "修改用户身份成功");


    }

    //根据userid删除用户数据
    @Override
    public void deleteByUserid(String userid) {
        userJpaRepository.deleteById(userid);
        userJpaRepository.flush();
    }

}
