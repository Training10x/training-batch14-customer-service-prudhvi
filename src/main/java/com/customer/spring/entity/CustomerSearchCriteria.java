package com.customer.spring.entity;


import lombok.Data;

@Data
public class CustomerSearchCriteria {
    private String name;
    private String customerEmail;
    private String industry;
    private Integer companySize;
    private String customerPhoneNumber;
    private String status;
    private String address;
}
