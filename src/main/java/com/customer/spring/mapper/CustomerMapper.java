package com.customer.spring.mapper;

import com.customer.spring.dto.CustomerDTO;
import com.customer.spring.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

    CustomerDTO toDto(Customer customer);

    Customer toEntity(CustomerDTO dto);
}
