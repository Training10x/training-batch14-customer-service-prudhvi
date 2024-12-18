package com.customer.spring.service;

import com.customer.spring.dto.CustomerDTO;
import com.customer.spring.entity.Customer;
import com.customer.spring.entity.CustomerSearchCriteria;
import com.customer.spring.exception.ConflictException;
import com.customer.spring.mapper.CustomerMapper;
import com.customer.spring.repository.CustomerRepository;
import com.sun.jdi.request.InvalidRequestStateException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerService(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    public long createCustomer(CustomerDTO customerDTO) {
        validateCustomerDTO(customerDTO);

        Customer customer = customerMapper.toEntity(customerDTO);

        CustomerDTO result = customerMapper.toDto(customerRepository.save(customer));
        return result.getId();
    }

    public CustomerDTO updateCustomer(long id, CustomerDTO customerDTO) {
        if(isNullOrEmpty(customerDTO.getName())){
            throw new InvalidRequestStateException("The name is either not entered or empty string");
        }
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer with ID " + id + " not found in the Database"));

        updateCustomerFields(customer, customerDTO);

        return customerMapper.toDto(customerRepository.save(customer));
    }

    public String statusToggle(long id, String status){

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer with ID " + id + " not found in the Database"));
        if (!status.equalsIgnoreCase("enabled") && !status.equalsIgnoreCase("disabled")) {
            throw new IllegalArgumentException("Invalid status value, it should be either enabled or disabled: " + status);
        }
        else{
            customer.setStatus(status);
        }


        customerRepository.save(customer);
        return "Customer details " +status+ " successfully";

    }

    public Map<String, Object> searchCustomers(CustomerSearchCriteria criteria, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        Page<Customer> customersPage = customerRepository.searchCustomers(
                criteria,
                pageable
        );

        List<CustomerDTO> results = customersPage.getContent().stream()
                .map(customerMapper::toDto)
                .toList();
        if(results.isEmpty()){
            throw new InvalidRequestStateException("No results with the provided data. Try again.");
        }
        Map<String, Object> response = new HashMap<>();
        response.put("total_count", customersPage.getTotalElements());
        response.put("page_count", customersPage.getTotalPages());
        response.put("current_page", page);
        response.put("results", results);

        return response;
    }



    //Helper methods
    private void validateCustomerDTO(CustomerDTO customerDTO) {

        setDefaultValuesIfNull(customerDTO);

        if (isNullOrEmpty(customerDTO.getName())) {
            throw new InvalidRequestStateException("The name is either not entered or empty string");
        }
        if (isNullOrEmpty(customerDTO.getIndustry())) {
            throw new InvalidRequestStateException("Please enter the Industry.");
        }
        if (customerDTO.getCompanySize() <= 0) {
            throw new InvalidRequestStateException("Company size is either less than 0 or not given");
        }
        if (isNullOrEmpty(customerDTO.getAddress())) {
            throw new InvalidRequestStateException("Please enter the address.");
        }
        if (isNullOrEmpty(customerDTO.getCustomerPhoneNumber())) {
            throw new InvalidRequestStateException("Please enter the phone number.");
        }
        if (isNullOrEmpty(customerDTO.getCustomerEmail())) {
            throw new InvalidRequestStateException("Please enter the Email address.");
        }
        if (isNullOrEmpty(customerDTO.getStatus())) {
            throw new InvalidRequestStateException("Please enter the status.");
        }
        if (!isValidStatus(customerDTO.getStatus())) {
            throw new InvalidRequestStateException("Status should be either 'enabled' or 'disabled'");
        }
        Optional<Customer> existingCustomer = customerRepository.findByCustomerEmail(customerDTO.getCustomerEmail());
        Optional<Customer> existingCustomerPhoneNumber = customerRepository.findByCustomerPhoneNumber(customerDTO.getCustomerPhoneNumber());
        if (existingCustomer.isPresent()) {
            throw new ConflictException("Email address already in use: " + customerDTO.getCustomerEmail());
        }
        if (existingCustomerPhoneNumber.isPresent()) {
            throw new ConflictException("Phone number already in use: " + customerDTO.getCustomerPhoneNumber());
        }
    }

    private void setDefaultValuesIfNull(CustomerDTO customerDTO) {
        if (customerDTO.getAddress() == null) {
            customerDTO.setAddress("defaultAddress");
        }
        if (customerDTO.getOtherCustomerData() == null) {
            customerDTO.setOtherCustomerData("defaultOther_Cust_data");
        }
    }

    private boolean isNullOrEmpty(String value) {
        return value == null || value.isEmpty();
    }

    private boolean isValidStatus(String status) {
        return "enabled".equals(status) || "disabled".equals(status);
    }

    private void updateCustomerFields(Customer customer, CustomerDTO customerDTO) {
        updateFieldIfValid(customerDTO.getName(), customer::setName);
        updateFieldIfValid(customerDTO.getIndustry(), customer::setIndustry);
        updateFieldIfValid(customerDTO.getCustomerEmail(), customer::setCustomerEmail);
        updateFieldIfValid(customerDTO.getAddress(), customer::setAddress);
        updateFieldIfValid(customerDTO.getCustomerPhoneNumber(), customer::setCustomerPhoneNumber);
        updateFieldIfValid(customerDTO.getOtherCustomerData(), customer::setOtherCustomerData);
        updateFieldIfValid(customerDTO.getStatus(), customer::setStatus);

        if (customerDTO.getCompanySize() != null && customerDTO.getCompanySize() > 0) {
            customer.setCompanySize(customerDTO.getCompanySize());
        }
    }

    private void updateFieldIfValid(String field, Consumer<String> setter) {
        if (field != null && !field.isEmpty()) {
            setter.accept(field);
        }
    }


}
