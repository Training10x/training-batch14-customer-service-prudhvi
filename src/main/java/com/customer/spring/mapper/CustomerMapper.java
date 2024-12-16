package com.customer.spring.mapper;

import com.customer.spring.dto.CustomerDTO;
import com.customer.spring.entity.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {
    public CustomerDTO toDto(Customer customer){
        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getId());
        dto.setName(customer.getName());
        dto.setIndustry(customer.getIndustry());
        dto.setCompanySize(customer.getCompanySize());
        dto.setCustomerEmail(customer.getCustomerEmail());
        dto.setCustomerPhoneNumber(customer.getCustomerPhoneNumber());
        dto.setAddress(customer.getAddress());
        dto.setOtherCustomerData(customer.getOtherCustomerData());
        dto.setStatus(customer.getStatus());

        return dto;
    }

    public Customer toEntity(CustomerDTO dto){
        Customer customer = new Customer();

        customer.setId(dto.getId());
        customer.setName(dto.getName());
        customer.setIndustry(dto.getIndustry());
        customer.setCompanySize(dto.getCompanySize());
        customer.setCustomerEmail(dto.getCustomerEmail());
        customer.setCustomerPhoneNumber(dto.getCustomerPhoneNumber());
        customer.setAddress(dto.getAddress());
        customer.setOtherCustomerData(dto.getOtherCustomerData());
        customer.setStatus(dto.getStatus());

        return customer;
    }
}
