package com.example.demo.Controller;

import com.example.demo.Service.DataService;
import com.example.demo.Utils.DataUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(value = "/user/device/sensor/data")
public class DataController {

    @Autowired
    private DataService dataService;

    /**
     * 获取历史数据
     * 描述：给定日期、devEUI、传感器类型，返回某日24时该传感器的数据
     */
    @ApiOperation(value = "获取某日历史数据", notes = "获取某日24时的指定设备传感器数据\n" +
            "如:20180701,0001000100010001,01 (typeid 01 指风速传感器)\n" +
            "20180701,0001000100010001,02,qiti  （typeid 02 指温湿度氨气三合一传感器)")
    @RequestMapping(value = "/getdatabydate", method = RequestMethod.POST)
    @ResponseBody
    @Cacheable(value="userCache") //缓存,这里没有指定key.
    public List<HashMap<String, String>> getdatabydate(@RequestParam(value = "date", required = true) String date, @RequestParam(value = "devEUI", required = true)
            String devEUI, @RequestParam(value = "typeid", required = true) String typeid, @RequestParam(value = "choice", required = false) String choice) {
        return DataUtil.getdatabydate(dataService, date, devEUI, typeid, choice);
    }

    /**
     * 获取历史数据
     * 描述：给定日期区间、devEUI、传感器类型，返回该时间段内该传感器数据
     */
    @ApiOperation(value = "获取某时段历史数据", notes = "获取给定时段的指定设备传感器数据")
    @RequestMapping(value = "/getdatafromdatetodate", method = RequestMethod.POST)
    @ResponseBody
    @Cacheable(value="userCache") //缓存,这里没有指定key. // 增加缓存机制，第一次请求慢，接下来请求会很快
    public List<HashMap<String, String>> getdatafromdatetodate(@RequestParam(value = "date1", required = true) String date1, @RequestParam(value = "date2", required = true) String date2,@RequestParam(value = "devEUI", required = true) String devEUI, @RequestParam(value = "typeid", required = true) String typeid, @RequestParam(value = "choice", required = false) String choice) {
        return DataUtil.getdatafromdatetodate(dataService, date1, date2, devEUI, typeid, choice);
    }


    /**
     * 获取实时数据
     * 描述：向客户端转发客户端需要的数据
     */
    @ApiOperation(value = "实时数据", notes = "获取指定设备传感器的实时数据")
    @RequestMapping(value = "/getrealtimedata", method = RequestMethod.GET)
    @ResponseBody
    public HashMap<String, String > getrealtimedata(@RequestParam(value = "devEUI", required = true) String devEUI, @RequestParam(value = "typeid", required = true) String typeid, @RequestParam(value = "choice", required = false) String choice) {
        return null;
    }

}
