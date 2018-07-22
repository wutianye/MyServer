package com.example.demo.Controller;


import com.example.demo.Annotation.CurrentUserid;
import com.example.demo.Entity.User;
import com.example.demo.Entity.UserToken;
import com.example.demo.Service.DataService;
import com.example.demo.Service.Impl.UserServiceImpl;
import com.example.demo.Service.UserService;
import com.example.demo.Service.UserTokenService;
import com.example.demo.Utils.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;

@CrossOrigin
@RestController
public class UserController {

    private final UserService userService;

    private final UserTokenService userTokenService;
    private final UserServiceImpl userServiceImpl;

    @Autowired
    public UserController(UserService userService, UserTokenService userTokenService, UserServiceImpl userServiceImpl) {
        this.userService = userService;
        this.userTokenService = userTokenService;
        this.userServiceImpl = userServiceImpl;
    }


    /**
     * 注册用户
     * 描述：注册用户均为普通用户
     */
    @ApiOperation(value = "注册用户", notes = "注册一个用户")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public String register(@RequestParam(value = "userid", required = true) String userid, @RequestParam(value = "password", required = true) String password) {
        User user = new User(userid, password, "common");
        return UserUtil.register(userService, user).toJSONString();
    }

    /**
     * 登陆
     * 描述：用户登录，web端提供普通用户和管理员登陆
     */
    @CrossOrigin
    @ApiOperation(value = "web端用户登陆", notes = "web端用户的登陆")
    @RequestMapping(value = "/weblogin", method = RequestMethod.POST)
    @ResponseBody
    public String weblogin(@RequestParam(value = "userid", required = true) String userid, @RequestParam(value = "password", required = true) String password) {
        return UserUtil.weblogin(userService, userTokenService, userid, password).toJSONStringWithToken();
    }

    /**
     * 登陆
     * 描述：用户登陆，Android端提供普通用户登陆
     */
    @ApiOperation(value = "app端用户登陆", notes = "app端用户的登陆")
    @RequestMapping(value = "/applogin", method = RequestMethod.POST)
    @ResponseBody
    public String applogin(@RequestParam(value = "userid", required = true) String userid, @RequestParam(value = "password", required = true) String password) {
        return UserUtil.applogin(userService, userTokenService, userid, password).toJSONStringWithToken();
    }


    /* *
     *
     * 功能描述:
     *
     * @param: [userId]
     * @return: com.example.demo.Utils.TMessage
     * @auther: liuyunxing
     * @Description 获取用户身份接口//TODO
     * @date: 2018/7/13 16:29
     */
    @ApiOperation(value = "获取用户信息", notes = "获取用户信息")
    @CrossOrigin
    @PostMapping("/userInfo")
    public TMessage getUserInfo(String token) {
        String role;
        HashMap res = new HashMap();
        UserToken userToken = userTokenService.findByToken(token); // 获得到userTOoken
        if (userToken != null) {
            User user = userServiceImpl.findByUserid(userToken.getUserid());// 获得到用户的id
            if (user != null) {
                role = user.getFlag();
                res.put("username", user.getUserid());
                if (!StringUtil.isEmpty(role) && StringUtil.isValidRole(role)) {
                    String[] roles = {role};
                    res.put("roles", roles);
                    return new TMessage(TMessage.CODE_SUCCESS, "获取用户身份成功", res);
                }
            }
        }
        String[] roles = {};
        res.put("roles", roles);
        return new TMessage(TMessage.CODE_FAILURE, "获取用户身份失败", res);
    }

    @CrossOrigin
    @PostMapping("/user/logout")
    public TMessage getUserInfo(){ // 退出登陆，服务器进行收尾操作

        return new TMessage(TMessage.CODE_SUCCESS,"退出成功");
    }

    @GetMapping("/user/allUserInfo")
    public TMessage getAllUserInfo(@RequestAttribute("currentUserid") String userid){ //获得用户的信息

        String role;
        if (userid != null) {
            User user = userServiceImpl.findByUserid(userid);// 获得到用户的id
            if (user != null) {
                role = user.getFlag();
                if (!role.equals("admin"))
                        return new TMessage(TMessage.CODE_FAILURE, "用户身份异常，不为admin用户");
                else {
                    return  new TMessage(TMessage.CODE_SUCCESS,"返回用户信息成功，包含用户的姓名和角色", userServiceImpl.getAllUserInfo(userid));
                }
            }
        }
        return new TMessage(TMessage.CODE_SUCCESS,"请重新登陆");
    }

    @PostMapping("/user/modifyPassword")

    // 修改用户密码
    public TMessage modifyPassword(@RequestAttribute("currentUserid") String userid, String modUserId,String password){

      return  userService.modifyPassword(userid,modUserId, password); // 修改密码
    }

    @PostMapping("/user/modifyRole")
    public TMessage modifyRole(@RequestAttribute("currentUserid") String userid, String modUserId, String role){
        return  userService.modifyUserRole(userid, modUserId, role);
    }

    //删除用户
    @ApiOperation(value = "删除用户", notes = "管理员删除用户及用户下所有信息")
    @RequestMapping(value = "/user/deleteUser", method = RequestMethod.POST)
    @ResponseBody
    public TMessage deleteUser(@RequestParam(value = "userid", required = true) String userid) {
        return DeleteUtil.deleteUser(userid);
    }

    /**
     * liuyunxing
     * @param userid
     * @return
     * 获得目录树形式的用户信息
     */
    @GetMapping("/user/getTreeUserInfo")
    public TMessage getTreeInfo(@RequestAttribute("currentUserid") String userid){
        return  userService.getTreeInfo(userid);
    }

    @GetMapping("/user/getTreeDeviceInfo")
    public TMessage getTreeDeviceInfo(@RequestAttribute("currentUserid") String userid ,String aimUserId){ // 获得制定用户的设别目录树信息表

        return  userService.getTreeDevice(userid, aimUserId);
    }

}
