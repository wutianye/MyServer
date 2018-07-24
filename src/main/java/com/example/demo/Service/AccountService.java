package com.example.demo.Service;

import com.example.demo.Entity.Account;

import java.util.List;


public interface AccountService {
    //注册:插入一条数据
    void insert(Account account);

    //根据id查询信息
    List<Account> findByUserid(String userid);


}
