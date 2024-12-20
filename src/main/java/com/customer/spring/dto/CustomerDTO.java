package com.customer.spring.dto;

import lombok.Data;

@Data
public class CustomerDTO {

    private Long id;
    private String name;

    private String industry;

    private Integer companySize;

    private String customerEmail;

    private String customerPhoneNumber;

    private String address;

    private String status;
    private String otherCustomerData;
}
