package com.example.demo.Controller;


import com.example.demo.Service.DeviceRelayService;
import com.example.demo.Service.RelaySwitchService;
import com.example.demo.Service.RelayTypeService;
import com.example.demo.Utils.DeleteUtil;
import com.example.demo.Utils.DeviceRelayUtil;
import com.example.demo.Utils.TMessage;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(value = "/user/devices")
public class DeviceRelayController {

    @Autowired
    private DeviceRelayService deviceRelayService;

    @Autowired
    private RelayTypeService relayTypeService;

    @Autowired
    private RelaySwitchService relaySwitchService;

    /**
     * 添加一个继电器
     * 描述：给指定的devEUI设备添加一个继电器
     */
    @ApiOperation(value = "添加继电器", notes = "为指定设备添加继电器")
    @RequestMapping(value = "/addRelay", method = RequestMethod.POST)
    @ResponseBody
    public String addsensor(@RequestParam(value = "devEUI", required = true)String devEUI, @RequestParam(value = "relayType", required = true) String relayType) {
        return DeviceRelayUtil.addsensor(deviceRelayService, devEUI, relayType).toJSONString();
    }

    /**
     * 获取继电器列表
     * 描述：获取给定的devEUI的所有继电器list
     */
    @ApiOperation(value = "获取继电器列表", notes = "获取当前设备下的继电器列表")
    @RequestMapping(value = "/getDataRelay", method = RequestMethod.GET)
    @ResponseBody
    public TMessage getRelay(@RequestParam(value = "devEUI", required = true) String devEUI) {
        List<HashMap<String,String>> res =  DeviceRelayUtil.getRelay(deviceRelayService, relayTypeService, devEUI);
        if (res != null && res.size()!= 0) {
            return  new TMessage(TMessage.CODE_SUCCESS, "获取成功",res);
        }
        return  new TMessage(TMessage.CODE_FAILURE, "获取失败,没有继电器数据");
    }

    /**
     * 获取继电器下开关的状态
     * 描述：获取指定继电器下所有的开关名称及状态
     */
    @ApiOperation(value = "获取继电器状态", notes = "获取指定继电器下的所有开关的名称及状态")
    @RequestMapping(value = "getRelaySwitch", method = RequestMethod.GET)
    @ResponseBody
    public TMessage getRelaySwitch(@RequestParam(value = "devEUI", required = true)String devEUI, @RequestParam(value = "relayType", required = true) String relayType) {
        List<HashMap<String ,Object >> res = DeviceRelayUtil.getRelaySwitch(relaySwitchService, devEUI, relayType);
        if (res != null && res.size()!= 0) {
            return  new TMessage(TMessage.CODE_SUCCESS, "获取成功",res);
        }
        return  new TMessage(TMessage.CODE_FAILURE, "获取失败,未找到继电器开关数据");
    }

    /**
     * 修改某开关的状态
     * 描述：改变继电器某一个开关状态
     */
    @ApiOperation(value = "更改继电器开关状态", notes = "更改指定设备下指定继电器指定开关的状态，switchId为获取开关列表时传回来的，state为0或1，代表关/开")
    @RequestMapping(value = "changeRelaySwitch", method = RequestMethod.POST)
    @ResponseBody
    public String changeRelaySwitch(@RequestParam(value = "devEUI", required = true)String devEUI, @RequestParam(value = "switchId", required = true) String switchId, @RequestParam(value = "state", required = true) String state) {
        return DeviceRelayUtil.changeRelaySwitch(devEUI, switchId, state).toJSONString();
    }

    /**
     * 修改继电器开关的状态
     * 描述：改变继电器多个开关状态
     */
    @ApiOperation(value = "更改继电器开关状态", notes = "更改指定设备下指定继电器多个开关的状态，需要前端给出devEUI和一个jsonObject.toString的信息，jsonObject中包含如下类似信息：{'02':'1','03':'0'}，其中key字段为开关的编号，value字段为开关状态0/1")
    @RequestMapping(value = "changeRelayMulSwitch", method = RequestMethod.POST)
    @ResponseBody
    public String changeRelayMulSwitch(@RequestParam(value = "devEUI", required = true)String devEUI, @RequestParam(value = "jsonstr", required = true) String jsonstr) {
        return DeviceRelayUtil.changeRelaySwitch(devEUI, jsonstr).toJSONString();
    }


    /**
     * 删除继电器
     * 描述：删除给定的继电器
     */
    @ApiOperation(value = "删除继电器", notes = "删除给定的继电器")
    @RequestMapping(value = "/deleteRelay", method = RequestMethod.POST)
    @ResponseBody
    public TMessage deleteDevice(@RequestParam(value = "devEUI", required = true) String devEUI, @RequestParam(value = "relaytype", required = true) String relaytype) {
        return DeleteUtil.deleteRelay(devEUI, relaytype);
    }
}
