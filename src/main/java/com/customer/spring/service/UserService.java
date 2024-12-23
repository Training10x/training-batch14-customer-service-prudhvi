package com.customer.spring.service;

import com.customer.spring.entity.Users;
import com.customer.spring.exception.AuthenticationFailedException;
import com.customer.spring.exception.ConflictException;
import com.customer.spring.exception.InvalidPasswordException;
import com.customer.spring.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationManager authManager;
    private final JWTService jwtService;
    private KafkaProducerService kafkaProd;

    public UserService(UserRepository userRepository,
                       AuthenticationManager authManager,
                       JWTService jwtService, KafkaProducerService kafka) {
        this.userRepository = userRepository;
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.kafkaProd = kafka;
    }

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);


    public Users register(Users user){

        if (!validatePassword(user.getPassword())) {
            throw new InvalidPasswordException("Password does not meet the required criteria");
        }
        Optional<Users> existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            throw new ConflictException("username already in use: " + user.getUsername());
        }

        user.setPassword(encoder.encode(user.getPassword()));
        kafkaProd.sendMessage("user-topic", "Message sent from Customer API register end point");
        return userRepository.save(user);
    }

    public String verify(Users user) {
        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );
            if (authentication.isAuthenticated()) {
                return jwtService.generateToken(user.getUsername());
            }
        } catch (AuthenticationException e) {
            throw new AuthenticationFailedException("Invalid username or password");
        }
        return "Unable to verify";
    }

    public boolean validatePassword(String password) {
        if(password == null || password.length() < 8){
            throw new InvalidPasswordException("Password does not meet the required criteria");
        }
        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;

        String specialCharacters = "!@#$%^&*()-+";

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUppercase = true;
            } else if (Character.isLowerCase(c)) {
                hasLowercase = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (specialCharacters.contains(String.valueOf(c))) {
                hasSpecialChar = true;
            }
        }
        return hasUppercase && hasLowercase && hasDigit && hasSpecialChar;
    }

}