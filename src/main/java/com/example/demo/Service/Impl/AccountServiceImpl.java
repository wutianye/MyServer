package com.example.demo.Service.Impl;

import com.example.demo.Entity.Account;
import com.example.demo.Repository.AccountJpaRepository;
import com.example.demo.Service.AccountService;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountServiceImpl implements AccountService{
    @Autowired
    AccountJpaRepository accountJpaRepository;

    @Override
    public void insert(Account account) {
        accountJpaRepository.save(account);
    }

    @Override
    public List<Account> findByUserid(String userid) {
        return accountJpaRepository.findAllByUserid(userid);
    }


}
