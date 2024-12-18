package com.customer.spring.service;

import com.customer.spring.entity.Users;
import com.customer.spring.exception.ConflictException;
import com.customer.spring.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationManager authManager;
    private final JWTService jwtService;

    public UserService(UserRepository userRepository,
                       AuthenticationManager authManager,
                       JWTService jwtService) {
        this.userRepository = userRepository;
        this.authManager = authManager;
        this.jwtService = jwtService;
    }

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);


    public Users register(Users user){
        Optional<Users> existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            throw new ConflictException("username already in use: " + user.getUsername());
        }
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public String verify(Users user) {
        Authentication authentication =
                authManager
                        .authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        if(authentication.isAuthenticated()){
            return jwtService.generateToken(user.getUsername());
        }
        return "fail";
    }
}