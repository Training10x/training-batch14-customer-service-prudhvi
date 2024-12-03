package com.customer.spring.controller;


import com.customer.spring.dto.CustomerDTO;
import com.customer.spring.entity.CustomerSearchCriteria;
import com.customer.spring.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/welcome")
    @ResponseStatus(HttpStatus.OK)
    public String welcome(){
        return ("Hello, welcome to the spring app");
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public long createCustomer(@RequestBody CustomerDTO customerDTO) {
        return customerService.createCustomer(customerDTO);
    }

    @PutMapping("{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable long id, @RequestBody CustomerDTO customerDTO){
        return ResponseEntity.ok(customerService.updateCustomer(id, customerDTO));

    }

    @PatchMapping("{id}")
    public ResponseEntity<String> statusToggle(@PathVariable long id, @RequestParam String status){
        if (!status.equalsIgnoreCase("enabled") && !status.equalsIgnoreCase("disabled")) {
            throw new IllegalArgumentException("Invalid status value: " + status);
        }
        return ResponseEntity.ok(customerService.statusToggle(id, status));

    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchCustomers(
            CustomerSearchCriteria criteria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    )
    {
        Map<String, Object> response = customerService.searchCustomers(criteria, page, limit);
        return ResponseEntity.ok(response);
    }










}
