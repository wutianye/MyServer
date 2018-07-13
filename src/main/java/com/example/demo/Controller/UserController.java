package com.example.demo.Controller;


import com.example.demo.Entity.User;
import com.example.demo.Service.DataService;
import com.example.demo.Service.UserService;
import com.example.demo.Service.UserTokenService;
import com.example.demo.Utils.UserUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserTokenService userTokenService;

    /**
     *注册用户
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

}
