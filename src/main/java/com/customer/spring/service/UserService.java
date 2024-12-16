package com.customer.spring.service;

import com.customer.spring.entity.Customer;
import com.customer.spring.entity.Users;
import com.customer.spring.exception.ConflictException;
import com.customer.spring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    public Users register(Users user){
        Optional<Users> existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            throw new ConflictException("username already in use: " + user.getUsername());
        }
        return userRepository.save(user);
    }
}