package com.example.demo.Controller;

import com.example.demo.Service.UserDeviceService;
import com.example.demo.Utils.TMessage;
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
    public List getdevices(@RequestAttribute("currentUserid") String userid) {
        return UserDeviceUtil.getdevices(userDeviceService, userid);
    }

    /* *
     *
     * 功能描述:
     *  获取设备列表详细信息，因为前一个接口并不适用于前端接口，所以复写接口，但原接口不变
     * @param: [userid]
     * @return: com.example.demo.Utils.TMessage
     * @auther: liuyunxing
     * @Description //TODO
     * @date: 2018/7/14 19:52
     */
    @ApiOperation(value = "获取设备列表", notes = "获取当前用户的设备列表")
    @RequestMapping(value = "/getDevices", method = RequestMethod.GET)
    @ResponseBody
    public TMessage getDevices(@RequestAttribute("currentUserid") String userid) {


        List list = UserDeviceUtil.getdevices(userDeviceService, userid);
        if (list != null) {
            return  new TMessage(TMessage.CODE_SUCCESS,"获取设备列表成功",list);
        }
        return new TMessage(TMessage.CODE_FAILURE, "获取设备列表失败",null);

    }

}
