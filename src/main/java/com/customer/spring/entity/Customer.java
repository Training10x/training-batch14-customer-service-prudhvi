package com.customer.spring.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "customers")
@Data
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String industry;
    private int companySize;
    private String customerEmail;
    private String customerPhoneNumber;
    private String address;
    private String status;
    private String otherCustomerData;
}