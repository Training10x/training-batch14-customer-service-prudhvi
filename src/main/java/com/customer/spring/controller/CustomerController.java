package com.customer.spring.controller;


import com.customer.spring.dto.CustomerDTO;
import com.customer.spring.dto.SavedCustomerResponse;
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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<SavedCustomerResponse> createCustomer(@RequestBody CustomerDTO customerDTO) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(customerService.createCustomer(customerDTO));
    }

    @PutMapping("{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable long id, @RequestBody CustomerDTO customerDTO){
        return ResponseEntity
                .ok(customerService.updateCustomer(id, customerDTO));

    }

    @PatchMapping("{id}")
    public ResponseEntity<Map<String, Object>> statusToggle(@PathVariable long id, @RequestParam String status){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(customerService.statusToggle(id, status));
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchCustomers(
            CustomerSearchCriteria criteria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    )
    {
        return ResponseEntity
                .ok(customerService.searchCustomers(criteria, page, limit));
    }
}
