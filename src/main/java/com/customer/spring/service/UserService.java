package com.customer.spring.service;

import com.customer.spring.entity.Users;
import com.customer.spring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    public Users register(Users user){
        System.out.println(user.toString());
        return userRepository.save(user);
    }
}