package com.example.demo.Controller;


import com.example.demo.Entity.Account;
import com.example.demo.Service.AccountService;
import com.example.demo.Service.Impl.AccountServiceImpl;
import io.swagger.annotations.ApiOperation;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
public class AccountController {
    private final AccountService accountService;
    private final AccountServiceImpl accountServiceImpl;
    @Autowired
    public AccountController(AccountService accountService, AccountServiceImpl accountServiceImpl) {
        this.accountService = accountService;
        this.accountServiceImpl = accountServiceImpl;
    }

    /**
     * 查询记录
     * 描述：任何用户都能添加记录
     */
    @CrossOrigin
    @ApiOperation(value = "用户查询记录", notes = "用户查询记录")
    @RequestMapping(value = "/user/account/{id}", method = RequestMethod.GET)
    String getAccount(@PathVariable("id") String id) {
        String response = "";
        List<Account> list = accountService.findByUserid(id);
        JSONArray jsonArray = new JSONArray(list);
        response = jsonArray.toString();
        return response;
    }

    @CrossOrigin
    @ApiOperation(value = "用户添加记录", notes = "用户添加记录")
    @RequestMapping(value = "/user/account", method = RequestMethod.PUT)
    String addAccount(@RequestBody String a) throws JSONException {
        System.out.println(a);
        JSONObject jsonObject = new JSONObject(a);
        System.out.println(jsonObject);
        Account account = new Account(jsonObject.getString("userid"),jsonObject.getString("date"),
                jsonObject.getString("money"),jsonObject.getString("device"),jsonObject.getString("type"));
        accountService.insert(account);
        Long result = account.getId();
        Map<String,Object> map = new HashMap<String, Object>();
        if(result != 0)
            map.put("message","添加成功");
        else
            map.put("message","添加失败");
        JSONObject mapJson = new JSONObject(map);
        return mapJson.toString();
    }
}
