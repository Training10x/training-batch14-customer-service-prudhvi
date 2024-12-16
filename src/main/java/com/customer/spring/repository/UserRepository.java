package com.customer.spring.repository;

import com.customer.spring.entity.Customer;
import com.customer.spring.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Integer> {
//    Users findByUsername(String username);
    Optional<Users> findByUsername(String username);
}
