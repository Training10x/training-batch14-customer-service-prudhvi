package com.customer.spring.controller;

import com.customer.spring.dto.CustomerDTO;
import com.customer.spring.entity.CustomerSearchCriteria;
import com.customer.spring.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createCustomer() {
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setName("John Doe");
        customerDTO.setCustomerEmail("john.doe@example.com");
        long expectedId = 1L;

        when(customerService.createCustomer(customerDTO)).thenReturn(expectedId);

        ResponseEntity<Map<String, Object>> response = customerController.createCustomer(customerDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedId, response.getBody().get("id"));
        assertEquals("Customer created successfully", response.getBody().get("message"));
        verify(customerService, times(1)).createCustomer(customerDTO);
    }


    @Test
    void updateCustomer() {
        // Arrange
        long customerId = 1L;
        CustomerDTO requestDTO = new CustomerDTO();
        requestDTO.setName("Updated Name");
        requestDTO.setCustomerEmail("updated.email@example.com");

        CustomerDTO responseDTO = new CustomerDTO();
        responseDTO.setId(customerId);
        responseDTO.setName("Updated Name");
        responseDTO.setCustomerEmail("updated.email@example.com");

        when(customerService.updateCustomer(customerId, requestDTO)).thenReturn(responseDTO);

        // Act
        ResponseEntity<CustomerDTO> response = customerController.updateCustomer(customerId, requestDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());

        verify(customerService, times(1)).updateCustomer(customerId, requestDTO);
    }

    @Test
    void statusToggle_ShouldReturnEnabledMessage_WhenStatusIsEnabled() {
        // Arrange
        long customerId = 1L;
        String newStatus = "enabled";
        String expectedMessage = "Customer details enabled successfully";
        Map<String, Object> expectedResponseMap = new HashMap<>();
        expectedResponseMap.put("status", expectedMessage);

        when(customerService.statusToggle(customerId, newStatus)).thenReturn(expectedMessage);

        // Act
        ResponseEntity<Map<String, Object>> response = customerController.statusToggle(customerId, newStatus);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedResponseMap, response.getBody());

        verify(customerService, times(1)).statusToggle(customerId, newStatus);
    }

    @Test
    void statusToggle_ShouldReturnDisabledMessage_WhenStatusIsDisabled() {
        // Arrange
        long customerId = 2L;
        String newStatus = "disabled";
        String expectedMessage = "Customer details disabled successfully";
        Map<String, Object> expectedResponseMap = new HashMap<>();
        expectedResponseMap.put("status", expectedMessage);

        when(customerService.statusToggle(customerId, newStatus)).thenReturn(expectedMessage);

        // Act
        ResponseEntity<Map<String, Object>> response = customerController.statusToggle(customerId, newStatus);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedResponseMap, response.getBody());

        verify(customerService, times(1)).statusToggle(customerId, newStatus);
    }




    @Test
    void searchCustomers() {
        // Arrange
        CustomerSearchCriteria criteria = new CustomerSearchCriteria();
        criteria.setName("John");
        criteria.setCustomerEmail("john.doe@example.com");
        criteria.setIndustry("IT");
        criteria.setCompanySize(50);
        criteria.setCustomerPhoneNumber("1234567890");
        criteria.setStatus("enabled");
        criteria.setAddress("123 Main St");

        Map<String, Object> expectedResponse = Map.of(
                "total_count", 10,
                "page_count", 1,
                "current_page", 0,
                "results", List.of(new CustomerDTO())
        );

        // Mocking the customerService method
        when(customerService.searchCustomers(criteria, 0, 10)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<Map<String, Object>> response = customerController.searchCustomers(criteria, 0, 10);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());

        // Verify that the service method was called
        verify(customerService, times(1)).searchCustomers(criteria, 0, 10);
    }
}