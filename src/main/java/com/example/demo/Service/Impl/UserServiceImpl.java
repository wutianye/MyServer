package com.example.demo.Service.Impl;

import com.example.demo.Entity.*;
import com.example.demo.Repository.UserJpaRepository;
import com.example.demo.Service.*;
import com.example.demo.Utils.StringUtil;
import com.example.demo.Utils.TMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class UserServiceImpl implements UserService{


    private  final String[] roles = {"admin","common","editor","visitor"};
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private UserDeviceServiceImpl userDeviceService; // 根据user查看用户所拥有的设备

    @Autowired
    private DeviceSensorService deviceSensorService; // 根据设备查找传感器

    @Autowired
    private DeviceRelayService deviceRelayService;  // 更具设备查找触发器

    @Autowired
    private SensorTypeService sensorTypeService;

    @Autowired
    private RelayTypeService relayTypeService;

    @Autowired

    private RelaySwitchService relaySwitchService;
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
        HashMap<String, Object> res = new HashMap<>();
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

    @Override
    public TMessage getTreeInfo(String userId) {
        User user = userJpaRepository.findUserByUserid(userId); // 查找user
        if (user == null) return new TMessage(TMessage.CODE_FAILURE, "token过期，请重新登陆再次请求");
        if (!user.getFlag().equals("admin")) return  new TMessage(TMessage.CODE_FAILURE, "您没有权限查看本界面");
        List<User> userList = userJpaRepository.findAll();// 找到所有的user
        if (userList == null) return new TMessage(TMessage.CODE_FAILURE, "获取用户信息失败");
        List<HashMap<String, Object>> userTreeInfo = new ArrayList<HashMap<String, Object>>(); //整个用户的树形信息，并且管理员无法查看其他管理员的信息
        userList.forEach(user1->{

            if (!user1.getFlag().equals("admin")){
                HashMap<String,Object> userNode = new HashMap<>(); // 一个节点的信息
                userNode.put("id",user1.getUserid());
                userNode.put("label",user1.getUserid());
                userNode.put("type","user");
                userNode.put("loading",false);
                userNode.put("loaded",false);
                userNode.put("icon","admin-user");
                userNode.put("children",new ArrayList<>());
                userTreeInfo.add(userNode);
            }
        });
        return new TMessage(TMessage.CODE_SUCCESS, "获取信息成功",userTreeInfo);
    }

    public TMessage getTreeDevice(String userId, String aimUserId){

        HashMap<String, Object> res = new HashMap<>();
        List  devChildren = new ArrayList();
        //验证身份和合法性
        User user = userJpaRepository.findUserByUserid(userId); // 查找user
        if (user == null) return new TMessage(TMessage.CODE_FAILURE, "token过期，请重新登陆再次请求");
        if (!user.getFlag().equals("admin")) return  new TMessage(TMessage.CODE_FAILURE, "您没有权限查看本界面");
        User aimUser = userJpaRepository.findUserByUserid(aimUserId);
        if (aimUser == null)  return  new TMessage(TMessage.CODE_FAILURE, "用户不存在，发生错误");
        // 查找用户下的所有devEui
        List<UserDevice> deviceList = userDeviceService.findAllByUserid(aimUserId);
        res.put("id","deviceDir"+userId);
        res.put("label","设备列表");
        res.put("icon","device-entry");
        res.put("type","deviceDir");

        List<HashMap<String,Object>> deviceChildren = new ArrayList<>();
        deviceList.forEach((device->{
            devChildren.add(this.getDeviceTree(device));
        }));
        res.put("children",devChildren);
        // children, 放触发器列表循环操作

        return  new TMessage(TMessage.CODE_SUCCESS,"获取成功",res);
    }
    // 获得设备列表
    private HashMap<String, Object> getDeviceTree(UserDevice userDevice){

        HashMap<String, Object> deviceNode = new HashMap<>();
        HashMap<String, Object> sensorList = new HashMap<>();
        HashMap<String, Object> triggerList = new HashMap<>();
        deviceNode.put("id",userDevice.getDevEUI());
        deviceNode.put("label", userDevice.getDevname());
        deviceNode.put("icon","device");
        deviceNode.put("type","device");
        //接下来是触发器列表和传感器列表
        //传感器列表，添加chidren
        sensorList.put("id",userDevice.getDevEUI()+"sensorDir" + Math.random());
        sensorList.put("label","传感器列表");
        sensorList.put("icon","sensor-entry");
        sensorList.put("type","sensorDir"); // 传感器列表

        List<DeviceSensor> sensorsList = deviceSensorService.findBydevEUI(userDevice.getDevEUI());
        List<HashMap<String, Object>> sensorsChild = new ArrayList<HashMap<String, Object>>();
        sensorsList.forEach((treeSensor->{
            sensorsChild.add(this.getSensor(treeSensor)); // chidren
        }));
        sensorList.put("children",sensorsChild); //具体的传感器列表

        triggerList.put("id", userDevice.getDevEUI() +"triggerDir" + Math.random());
        triggerList.put("label","继电器列表");
        triggerList.put("icon","control-entry");
        triggerList.put("type","triggerDir");
        //触发器列表，添加children
        List<DeviceRelay> controlLit = deviceRelayService.findBydevEUI(userDevice.getDevEUI()); //获得触发器列表
        List<HashMap<String, Object>> controlChildList = new ArrayList<HashMap<String, Object>>();
        controlLit.forEach((control->{
            controlChildList.add(this.getDelaySensor(control));
        }));
        triggerList.put("children",controlChildList); //触发器列表
        List<HashMap<String,Object>> deviceEleList = new ArrayList<HashMap<String,Object>>();
        deviceEleList.add(triggerList);
        deviceEleList.add(sensorList);
        deviceNode.put("children",deviceEleList);
        return  deviceNode;
    }
    // 获得传感器列表
    private HashMap<String, Object> getSensor(DeviceSensor deviceSensor){

        HashMap<String, Object> sensorTree = new HashMap<>();
        sensorTree.put("id",deviceSensor.getDevEUI() + deviceSensor.getDevEUI() + Math.random());
        sensorTree.put("status",deviceSensor.getState().equals("1"));
        sensorTree.put("type","sensor"); //传感器
        sensorTree.put("ctype","传感器"); //传感器
        if (deviceSensor.getTypeid().equals("01")){ //如果这个传感器是01
            sensorTree.put("icon","wind");
            sensorTree.put("label","风速传感器");
        }else if (deviceSensor.getTypeid().equals("02")){
            sensorTree.put("icon","complex"); //温湿度三合一
            sensorTree.put("label","温湿度三合一传感器");
        }
        return  sensorTree;
    }
    //获得设备列表
    private HashMap<String, Object> getDelaySensor(DeviceRelay deviceRelay){

        RelayType relayType = relayTypeService.getaRelayType(deviceRelay.getRelayType());
        HashMap<String, Object> delaySensor = new HashMap<>();
        delaySensor.put("id",deviceRelay.getDevEUI()  + deviceRelay.getRelayType() + Math.random());
        delaySensor.put("status", true);
        delaySensor.put("label",relayType.getRelayName());
        delaySensor.put("icon","control");
        delaySensor.put("type","controlDevice");
        // 添加child
        List<HashMap<String, Object>> child = new ArrayList<HashMap<String, Object>>();
        List<RelaySwitch> relaySwitches = relaySwitchService.findByrelayType(deviceRelay.getRelayType());
        relaySwitches.forEach((relaySwitch -> {
            HashMap<String ,Object> one = new HashMap<>();
            one.put("id",relaySwitch.getSwitchId()+deviceRelay.getDevEUI());
            one.put("status","false"); //触发器状态
            one.put("label", relaySwitch.getSwitchName());
            one.put("icon", "control");
            one.put("type","control"); //控制器
            one.put("ctype","继电器开关"); //控制器
            child.add(one);
        }));
        delaySensor.put("children",child);
        return  delaySensor;
    }

}
