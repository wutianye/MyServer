package com.example.demo.Controller;


import com.example.demo.Service.SensorTypeService;
import com.example.demo.Utils.SensorTypeUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;


@CrossOrigin
@RestController
@RequestMapping(value = "/sensortype")
public class SensorTypeController {

    @Autowired
    private SensorTypeService sensorTypeService;

    /**
     * 获取所有传感器类型列表
     * 描述：获取所有传感器类型列表
     */
    @ApiOperation(value = "获取传感器类型列表", notes = "获取系统支持的所有类型的传感器列表")
    @RequestMapping(value = "/getall", method = RequestMethod.GET)
    @ResponseBody
    public List<HashMap<String, String>> getall() {
        return SensorTypeUtil.getall(sensorTypeService);
    }

}
