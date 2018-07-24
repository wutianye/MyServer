package com.example.demo.Repository;

import com.example.demo.Entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountJpaRepository extends JpaRepository<Account,String>{

    List<Account> findAllByUserid(String userid);



}
