package com.example.demo.Controller;

import com.example.demo.Service.UserDeviceService;
import com.example.demo.Utils.UserDeviceUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(value = "/user")
public class UserDeviceController {

    @Autowired
    private UserDeviceService userDeviceService;
    /**
     * 添加一个设备
     * 描述：给定devEUI、devname及userid为用户添加设备
     */
    @ApiOperation(value = "添加设备", notes = "添加一个设备")
    @RequestMapping(value = "/adddevice", method = RequestMethod.POST)
    @ResponseBody
    public String adddevice(@RequestParam(value = "devEUI", required = true) String devEUI, @RequestParam(value = "devname", required = true) String devname, @RequestAttribute("currentUserid") String userid, @RequestParam(value = "longitude", required = false) String longitude, @RequestParam(value = "latitude", required = false) String latitude, @RequestParam(value = "address", required = false) String address) {
        return UserDeviceUtil.adddevice(userDeviceService, devEUI, devname, userid, longitude, latitude, address).toJSONString();
    }

    /**
     * 获取设备列表
     * 描述：获取给定用户的设备列表
     */
    @ApiOperation(value = "获取设备列表", notes = "获取当前用户的设备列表")
    @RequestMapping(value = "/getdevices", method = RequestMethod.GET)
    @ResponseBody
    public List<HashMap<String, String>> getdevices(@RequestAttribute("currentUserid") String userid) {
        return UserDeviceUtil.getdevices(userDeviceService, userid);
    }

}
