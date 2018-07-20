package com.example.demo.Controller;


import com.example.demo.Service.RelayTypeService;
import com.example.demo.Utils.RelayTypeUtil;
import com.example.demo.Utils.SensorTypeUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(value = "/relaytype")
public class RelayTypeController {

    @Autowired
    private RelayTypeService relayTypeService;

    /**
     * 获取所有继电器类型列表
     * 描述：获取所有继电器类型列表
     */
    @ApiOperation(value = "获取继电器类型列表", notes = "获取系统支持的所有类型的继电器列表")
    @RequestMapping(value = "/getall", method = RequestMethod.GET)
    @ResponseBody
    public List<HashMap<String, String>> getall() {
        return RelayTypeUtil.getall(relayTypeService);
    }
}
