package com.example.demo.Controller;


import com.example.demo.Entity.DeviceSensor;
import com.example.demo.Service.DeviceSensorService;
import com.example.demo.Service.SensorTypeService;
import com.example.demo.Utils.DeviceSensorUtil;
import com.example.demo.Utils.Info;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(value = "/user/devices")
public class DeviceSensorController {
    @Autowired
    private DeviceSensorService deviceSensorService;

    @Autowired
    private SensorTypeService sensorTypeService;

    /**
     * 添加一个传感器
     * 描述：给指定的devEUI设备添加一个传感器
     */
    @ApiOperation(value = "添加传感器", notes = "为指定设备添加传感器")
    @RequestMapping(value = "/addsensor", method = RequestMethod.POST)
    @ResponseBody
    public String addsensor(@RequestParam(value = "devEUI", required = true)String devEUI, @RequestParam(value = "typeid", required = true) String typeid) {
        return DeviceSensorUtil.addsensor(deviceSensorService, devEUI, typeid).toJSONString();
    }

    /**
     * 获取传感器列表
     * 描述：获取给定的devEUI的所有传感器list
     */
    @ApiOperation(value = "获取传感器列表", notes = "获取当前设备下的传感器列表")
    @RequestMapping(value = "/getsensor", method = RequestMethod.GET)
    @ResponseBody
    public List<HashMap<String,String>> getsensor(@RequestParam(value = "devEUI", required = true) String devEUI) {
        return DeviceSensorUtil.getsensor(deviceSensorService, sensorTypeService, devEUI);
    }

    /**
     * 更改传感器状态
     * 描述：更改给定devEUI下某typeid对应的传感器的开关状态
     */
    @ApiOperation(value = "修改传感器状态", notes = "修改传感器的状态")
    @RequestMapping(value = "/changesensor", method = RequestMethod.POST)
    @ResponseBody
    public String changesensor(@RequestParam(value = "devEUI", required = true) String devEUI, @RequestParam(value = "typeid", required = true) String typeid, @RequestParam(value = "state", required = true) String state) {
        DeviceSensor deviceSensor = new DeviceSensor(devEUI, typeid, state);
        return DeviceSensorUtil.updatestate(deviceSensorService, deviceSensor).toJSONString();
    }

}
