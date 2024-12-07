package com.customer.spring.service;

import com.customer.spring.dto.CustomerDTO;
import com.customer.spring.entity.Customer;
import com.customer.spring.entity.CustomerSearchCriteria;
import com.customer.spring.exception.ConflictException;
import com.customer.spring.mapper.CustomerMapper;
import com.customer.spring.repository.CustomerRepository;
import com.sun.jdi.request.InvalidRequestStateException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() { // Yet to do.
    }

    @Test
    void createCustomer_ShouldCreateCustomer_WhenEmailNotInUse() {
        // Arrange
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setId(1L);
        customerDTO.setName("John Doe");
        customerDTO.setCustomerEmail("test@example.com");
        customerDTO.setIndustry("Tech");
        customerDTO.setCompanySize(50);
        customerDTO.setAddress("123 Main Street");
        customerDTO.setCustomerPhoneNumber("1234567890");
        customerDTO.setStatus("enabled");

        Customer customerEntity = new Customer();
        customerEntity.setId(1L);
        customerEntity.setName("John Doe");
        customerEntity.setCustomerEmail("test@example.com");

        when(customerRepository.findByCustomerEmail(anyString()))
                .thenReturn(Optional.empty());
        when(customerMapper.toEntity(any(CustomerDTO.class)))
                .thenReturn(customerEntity);
        when(customerRepository.save(any(Customer.class)))
                .thenReturn(customerEntity);
        when(customerMapper.toDto(any(Customer.class)))
                .thenReturn(customerDTO);

        // Act
        long id = customerService.createCustomer(customerDTO);

        // Assert
        assertEquals(1L, id);
        verify(customerRepository, times(1)).findByCustomerEmail("test@example.com");
        verify(customerRepository, times(1)).save(customerEntity);
    }

    @Test
    void createCustomer_ShouldCreateCustomer_WhenPhoneNumberNotInUse() {
        // Arrange
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setId(1L);
        customerDTO.setName("John Doe");
        customerDTO.setCustomerEmail("test@example.com");
        customerDTO.setIndustry("Tech");
        customerDTO.setCompanySize(50);
        customerDTO.setAddress("123 Main Street");
        customerDTO.setCustomerPhoneNumber("1234567890");
        customerDTO.setStatus("enabled");

        Customer customerEntity = new Customer();
        customerEntity.setId(1L);
        customerEntity.setName("John Doe");
        customerEntity.setCustomerEmail("test@example.com");

        when(customerRepository.findByCustomerPhoneNumber(anyString()))
                .thenReturn(Optional.empty());
        when(customerMapper.toEntity(any(CustomerDTO.class)))
                .thenReturn(customerEntity);
        when(customerRepository.save(any(Customer.class)))
                .thenReturn(customerEntity);
        when(customerMapper.toDto(any(Customer.class)))
                .thenReturn(customerDTO);

        // Act
        long id = customerService.createCustomer(customerDTO);

        // Assert
        assertEquals(1L, id);
        verify(customerRepository, times(1)).findByCustomerPhoneNumber("1234567890");
        verify(customerRepository, times(1)).save(customerEntity);
    }

    @Test
    void createCustomer_ShouldThrowConflictException_WhenEmailAlreadyInUse() {
        // Arrange
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setCustomerEmail("test@example.com");

        Customer existingCustomer = new Customer();
        existingCustomer.setCustomerEmail("test@example.com");

        when(customerRepository.findByCustomerEmail("test@example.com"))
                .thenReturn(Optional.of(existingCustomer));

        ConflictException exception = assertThrows(ConflictException.class,
                () -> customerService.createCustomer(customerDTO));

        assertEquals("Email address already in use: test@example.com", exception.getMessage());
        verify(customerRepository, times(1)).findByCustomerEmail("test@example.com");
        verify(customerRepository, never()).save(any());
    }

    @Test
    void createCustomer_ShouldThrowConflictException_WhenPhoneNumberAlreadyInUse() {
        // Arrange
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setCustomerPhoneNumber("1234567890");

        Customer existingCustomer = new Customer();
        existingCustomer.setCustomerPhoneNumber("1234567890");

        when(customerRepository.findByCustomerPhoneNumber("1234567890"))
                .thenReturn(Optional.of(existingCustomer));

        ConflictException exception = assertThrows(ConflictException.class,
                () -> customerService.createCustomer(customerDTO));

        assertEquals("Phone number already in use: 1234567890", exception.getMessage());
        verify(customerRepository, times(1)).findByCustomerPhoneNumber("1234567890");
        verify(customerRepository, never()).save(any());
    }

    @Test
    void createCustomer_ShouldThrowInvalidRequestStateException_WhenRequiredFieldsAreMissing() {
        // Arrange
        CustomerDTO customerDTO = new CustomerDTO(); // Missing required fields

        // Act & Assert
        InvalidRequestStateException exception = assertThrows(InvalidRequestStateException.class,
                () -> customerService.createCustomer(customerDTO));

        assertEquals("The name is either not entered or empty string", exception.getMessage());
        verify(customerRepository, never()).findByCustomerEmail(anyString());
        verify(customerRepository, never()).save(any());
    }

    @Test
    void createCustomer_ShouldSetDefaultValues_WhenFieldsAreNull() {
        // Arrange
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setId(1L);
        customerDTO.setName("John Doe");
        customerDTO.setCustomerEmail("test@example.com");
        customerDTO.setIndustry("Tech");
        customerDTO.setCompanySize(50);
        customerDTO.setCustomerPhoneNumber("1234567890");
        customerDTO.setStatus("enabled");

        Customer customerEntity = new Customer();
        customerEntity.setId(1L);
        customerEntity.setName("John Doe");
        customerEntity.setCustomerEmail("test@example.com");
        customerEntity.setAddress("defaultAddress");
        customerEntity.setOtherCustomerData("defaultOther_Cust_data");

        when(customerRepository.findByCustomerEmail(anyString()))
                .thenReturn(Optional.empty());
        when(customerMapper.toEntity(any(CustomerDTO.class)))
                .thenAnswer(invocation -> {
                    CustomerDTO inputDTO = invocation.getArgument(0);
                    customerEntity.setAddress(inputDTO.getAddress());
                    customerEntity.setOtherCustomerData(inputDTO.getOtherCustomerData());
                    return customerEntity;
                });
        when(customerRepository.save(any(Customer.class)))
                .thenReturn(customerEntity);
        when(customerMapper.toDto(any(Customer.class)))
                .thenReturn(customerDTO);

        // Act
        long id = customerService.createCustomer(customerDTO);

        // Assert
        assertEquals(1L, id);
        assertEquals("defaultAddress", customerDTO.getAddress());
        assertEquals("defaultOther_Cust_data", customerDTO.getOtherCustomerData());
        verify(customerRepository, times(1)).findByCustomerEmail("test@example.com");
        verify(customerRepository, times(1)).save(customerEntity);
    }

    @Test
    void createCustomer_ShouldThrowInvalidRequestStateException_WhenIndustryIsMissingOrEmpty() {
        // Arrange
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setName("John Doe");
        customerDTO.setCustomerEmail("test@example.com");
        customerDTO.setIndustry(null);

        // Act & Assert
        InvalidRequestStateException exception = assertThrows(InvalidRequestStateException.class,
                () -> customerService.createCustomer(customerDTO));

        assertEquals("Please enter the Industry.", exception.getMessage());
        verify(customerRepository, times(1)).findByCustomerEmail(anyString());
        verify(customerRepository, never()).save(any());
    }

    @Test
    void createCustomer_ShouldThrowInvalidRequestStateException_WhenCompanySizeIsInvalid() {
        // Arrange
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setName("John Doe");
        customerDTO.setCustomerEmail("test@example.com");
        customerDTO.setIndustry("Tech");
        customerDTO.setCompanySize(0); // Invalid size

        // Act & Assert
        InvalidRequestStateException exception = assertThrows(InvalidRequestStateException.class,
                () -> customerService.createCustomer(customerDTO));

        assertEquals("Company size is either less than 0 or not given", exception.getMessage());
        verify(customerRepository, times(1)).findByCustomerEmail(anyString());
        verify(customerRepository, never()).save(any());
    }

    @Test
    void createCustomer_ShouldThrowInvalidRequestStateException_WhenAddressIsMissingOrEmpty() {
        // Arrange
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setName("John Doe");
        customerDTO.setCustomerEmail("test@example.com");
        customerDTO.setIndustry("Tech");
        customerDTO.setCompanySize(50);
        customerDTO.setCustomerPhoneNumber("1234567890");
        customerDTO.setStatus("enabled");
        customerDTO.setAddress("");

        // Act & Assert
        InvalidRequestStateException exception = assertThrows(InvalidRequestStateException.class,
                () -> customerService.createCustomer(customerDTO));

        assertEquals("Please enter the address.", exception.getMessage());
        verify(customerRepository, times(1)).findByCustomerEmail(anyString());
        verify(customerRepository, never()).save(any());
    }

    @Test
    void createCustomer_ShouldThrowInvalidRequestStateException_WhenPhoneNumberIsMissingOrEmpty() {
        // Arrange
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setName("John Doe");
        customerDTO.setCustomerEmail("test@example.com");
        customerDTO.setIndustry("Tech");
        customerDTO.setCompanySize(50);
        customerDTO.setAddress("123 Main Street");
        customerDTO.setCustomerPhoneNumber(null);

        InvalidRequestStateException exception = assertThrows(InvalidRequestStateException.class,
                () -> customerService.createCustomer(customerDTO));

        assertEquals("Please enter the phone number.", exception.getMessage());
        verify(customerRepository, times(1)).findByCustomerEmail(anyString());
        verify(customerRepository, never()).save(any());
    }

    @Test
    void createCustomer_ShouldThrowInvalidRequestStateException_WhenStatusIsMissingOrEmpty() {
        // Arrange
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setName("John Doe");
        customerDTO.setCustomerEmail("test@example.com");
        customerDTO.setIndustry("Tech");
        customerDTO.setCompanySize(50);
        customerDTO.setAddress("123 Main Street");
        customerDTO.setCustomerPhoneNumber("1234567890");
        customerDTO.setStatus(null);

        // Act & Assert
        InvalidRequestStateException exception = assertThrows(InvalidRequestStateException.class,
                () -> customerService.createCustomer(customerDTO));

        assertEquals("Please enter the status.", exception.getMessage());
        verify(customerRepository, times(1)).findByCustomerEmail(anyString());
        verify(customerRepository, never()).save(any());
    }

    @Test
    void createCustomer_ShouldThrowInvalidRequestStateException_WhenStatusIsInvalid() {
        // Arrange
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setName("John Doe");
        customerDTO.setCustomerEmail("test@example.com");
        customerDTO.setIndustry("Tech");
        customerDTO.setCompanySize(50);
        customerDTO.setAddress("123 Main Street");
        customerDTO.setCustomerPhoneNumber("1234567890");
        customerDTO.setStatus("invalid");

        // Act & Assert
        InvalidRequestStateException exception = assertThrows(InvalidRequestStateException.class,
                () -> customerService.createCustomer(customerDTO));

        assertEquals("Status should be either 'enabled' or 'disabled'", exception.getMessage());
        verify(customerRepository, times(1)).findByCustomerEmail(anyString());
        verify(customerRepository, never()).save(any());
    }








    @Test
    void updateCustomer_ShouldUpdateCustomer_WhenCustomerExists() {
        // Arrange
        long customerId = 1L;

        CustomerDTO requestDTO = new CustomerDTO();
        requestDTO.setName("Updated Name");
        requestDTO.setCustomerEmail("updated.email@example.com");

        Customer existingCustomer = new Customer();
        existingCustomer.setId(customerId);
        existingCustomer.setName("Old Name");
        existingCustomer.setCustomerEmail("old.email@example.com");

        Customer updatedCustomer = new Customer();
        updatedCustomer.setId(customerId);
        updatedCustomer.setName("Updated Name");
        updatedCustomer.setCustomerEmail("updated.email@example.com");

        CustomerDTO responseDTO = new CustomerDTO();
        responseDTO.setId(customerId);
        responseDTO.setName("Updated Name");
        responseDTO.setCustomerEmail("updated.email@example.com");

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(existingCustomer))
                .thenReturn(updatedCustomer);
        when(customerMapper.toDto(updatedCustomer))
                .thenReturn(responseDTO);

        CustomerDTO result = customerService.updateCustomer(customerId, requestDTO);

        assertNotNull(result);
        assertEquals(responseDTO, result);
        verify(customerRepository, times(1)).findById(customerId);
        verify(customerRepository, times(1)).save(existingCustomer);
        verify(customerMapper, times(1)).toDto(updatedCustomer);
    }

    @Test
    void updateCustomer_ShouldThrowEntityNotFoundException_WhenCustomerDoesNotExist() {
        long customerId = 1L;
        CustomerDTO requestDTO = new CustomerDTO();
        requestDTO.setName("SampleName");

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> customerService.updateCustomer(customerId, requestDTO));

        assertEquals("Customer with ID " + customerId + " not found in the Database", exception.getMessage());
        verify(customerRepository, times(1)).findById(customerId);
        verify(customerRepository, never()).save(any());
        verify(customerMapper, never()).toDto(any());
    }

    @Test
    void testUpdateCustomer_InvalidName_ThrowsInvalidRequestStateException() {
        long customerId = 1L;
        CustomerDTO invalidCustomerDTO = new CustomerDTO();
        invalidCustomerDTO.setName(""); // Invalid name (empty string)

        InvalidRequestStateException exception = assertThrows(
                InvalidRequestStateException.class,
                () -> customerService.updateCustomer(customerId, invalidCustomerDTO)
        );

        assertEquals("The name is either not entered or empty string", exception.getMessage());

        verifyNoInteractions(customerRepository);
    }

    @Test
    void statusToggle_ShouldUpdateStatus_WhenCustomerExistsAndStatusIsValid() {
        // Arrange
        long customerId = 1L;
        String newStatus = "enabled";

        Customer existingCustomer = new Customer();
        existingCustomer.setId(customerId);
        existingCustomer.setStatus("disabled");

        Customer updatedCustomer = new Customer();
        updatedCustomer.setId(customerId);
        updatedCustomer.setStatus(newStatus);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(existingCustomer)).thenReturn(updatedCustomer);

        String response = customerService.statusToggle(customerId, newStatus);

        assertNotNull(response);
        assertEquals("Customer details enabled successfully", response);
        verify(customerRepository, times(1)).findById(customerId);
        verify(customerRepository, times(1)).save(existingCustomer);
    }
    @Test
    void statusToggle_ShouldThrowEntityNotFoundException_WhenCustomerDoesNotExist() {
        // Arrange
        long customerId = 1L;
        String newStatus = "enabled";

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> customerService.statusToggle(customerId, newStatus));

        assertEquals("Customer with ID " + customerId + " not found in the Database", exception.getMessage());
        verify(customerRepository, times(1)).findById(customerId);
        verify(customerRepository, never()).save(any());
    }

    @Test
    void statusToggle_ShouldThrowIllegalArgumentException_WhenStatusIsInvalid() {
        // Arrange
        long customerId = 1L;
        String invalidStatus = "unknown";

        Customer existingCustomer = new Customer();
        existingCustomer.setId(customerId);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> customerService.statusToggle(customerId, invalidStatus));

        assertEquals("Invalid status value, it should be either enabled or disabled: " + invalidStatus, exception.getMessage());
        verify(customerRepository, times(1)).findById(customerId);
        verify(customerRepository, never()).save(any());
    }

    @Test
    void testSearchCustomers() {
        String name = "John Doe";
        String customerEmail = "john.doe@example.com";
        String industry = "Technology";
        Integer companySize = 100;
        String customerPhoneNumber = "1234567890";
        String status = "enabled";
        String address = "123 Main St";

        Customer customer1 = new Customer();
        customer1.setName("John Doe");
        customer1.setCustomerEmail(customerEmail);
        customer1.setCustomerPhoneNumber(customerPhoneNumber);
        customer1.setIndustry(industry);
        customer1.setCompanySize(companySize);
        customer1.setStatus(status);
        customer1.setAddress(address);
        Customer customer2 = new Customer();
        customer2.setName("John Smith");
        customer2.setCustomerEmail("john.smith@example.com");
        customer2.setCustomerPhoneNumber("9876543210");
        customer2.setIndustry("Technology");
        customer2.setCompanySize(100);
        customer2.setStatus("enabled");
        customer2.setAddress(address);


        List<Customer> customerList = Arrays.asList(customer1, customer2);
        Page<Customer> expectedPage = new PageImpl<>(customerList);

        CustomerDTO customerDto1 = new CustomerDTO();
        customerDto1.setName("John Doe");
        customerDto1.setCustomerEmail(customerEmail);
        customerDto1.setCustomerPhoneNumber(customerPhoneNumber);
        customerDto1.setIndustry("Technology");
        customerDto1.setCompanySize(100);
        customerDto1.setStatus("enabled");
        customerDto1.setAddress(address);

        CustomerDTO customerDto2 = new CustomerDTO();
        customerDto2.setName("John Smith");
        customerDto2.setCustomerEmail("john.smith@example.com");
        customerDto2.setCustomerPhoneNumber("9876543210");
        customerDto2.setIndustry("Technology");
        customerDto2.setCompanySize(100);
        customerDto2.setStatus("enabled");
        customerDto2.setAddress(address);
        List<CustomerDTO> expectedDtos = Arrays.asList(customerDto1, customerDto2);

        // Create the search criteria object
        CustomerSearchCriteria searchCriteria = new CustomerSearchCriteria();
        searchCriteria.setName(name);
        searchCriteria.setCustomerEmail(customerEmail);
        searchCriteria.setIndustry(industry);
        searchCriteria.setStatus(status);
        searchCriteria.setCustomerPhoneNumber(customerPhoneNumber);
        searchCriteria.setCompanySize(companySize);
        searchCriteria.setAddress(address);

        // Mock behavior
        when(customerRepository.searchCustomers(eq(searchCriteria), any(Pageable.class)))
                .thenReturn(expectedPage);

        when(customerMapper.toDto(any(Customer.class))).thenReturn(customerDto1, customerDto2);

        // Create the Pageable object
        Pageable pageable = PageRequest.of(0, 10);

        // When: Call the method under test
        Map<String, Object> response = customerService.searchCustomers(searchCriteria, 0, 10);

        // Then: Verify the result
        assertNotNull(response);
        assertEquals(2L, response.get("total_count"));
        assertEquals(1, response.get("page_count"));
        assertEquals(0, response.get("current_page"));
        assertEquals(expectedDtos, response.get("results"));



        verify(customerRepository, times(1)).searchCustomers(searchCriteria, pageable);


        verify(customerMapper, times(2)).toDto(any(Customer.class));
    }

    @Test
    void testSearchCustomersThrowsInvalidRequestStateException() {
        String name = "NonExistentName";
        String customerEmail = "nonexistent@example.com";
        String industry = "UnknownIndustry";
        Integer companySize = 1000;
        String customerPhoneNumber = "0000000000";
        String status = "inactive";
        String address = "Unknown Address";

        CustomerSearchCriteria searchCriteria = new CustomerSearchCriteria();
        searchCriteria.setName(name);
        searchCriteria.setCustomerEmail(customerEmail);
        searchCriteria.setIndustry(industry);
        searchCriteria.setStatus(status);
        searchCriteria.setCustomerPhoneNumber(customerPhoneNumber);
        searchCriteria.setCompanySize(companySize);
        searchCriteria.setAddress(address);

        Pageable pageable = PageRequest.of(0, 10);

        when(customerRepository.searchCustomers((searchCriteria), (pageable)))
                .thenReturn(Page.empty());

        InvalidRequestStateException exception = assertThrows(InvalidRequestStateException.class,
                () -> customerService.searchCustomers(searchCriteria, 0, 10));

        assertEquals("No results with the provided data. Try again.", exception.getMessage());

        verify(customerRepository, times(1)).searchCustomers((searchCriteria),(pageable));
        verifyNoInteractions(customerMapper);
    }

    @Test
    void testSearchCustomers_validData() {
        // Arrange
        CustomerSearchCriteria criteria = new CustomerSearchCriteria();
        criteria.setName("John");
        Pageable pageable = PageRequest.of(0, 10);

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("John Doe");
        customer.setCustomerEmail("john@example.com");
        customer.setIndustry("Tech");
        customer.setCompanySize(50);
        customer.setCustomerPhoneNumber("123-456-7890");
        customer.setStatus("Active");
        customer.setAddress("NY");

        List<Customer> customers = List.of(customer);

        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setId(1L);
        customerDTO.setName("John Doe");
        customerDTO.setCustomerEmail("john@example.com");
        customerDTO.setIndustry("Tech");
        customerDTO.setCompanySize(50);
        customerDTO.setCustomerPhoneNumber("123-456-7890");
        customerDTO.setStatus("Active");
        customerDTO.setAddress("NY");

        List<CustomerDTO> customerDTOs = List.of(customerDTO);

        Page<Customer> customersPage = new PageImpl<>(customers, pageable, customers.size());

        Mockito.when(customerRepository.searchCustomers(criteria, pageable)).thenReturn(customersPage);
        Mockito.when(customerMapper.toDto(Mockito.any(Customer.class))).thenReturn(customerDTOs.get(0));

        Map<String, Object> response = customerService.searchCustomers(criteria, 0, 10);

        assertNotNull(response);
        assertEquals(1L, response.get("total_count"));
        assertEquals(1, response.get("page_count"));
        assertEquals(0, response.get("current_page"));
        assertEquals(customerDTOs, response.get("results"));

        Mockito.verify(customerRepository, Mockito.times(1)).searchCustomers(criteria, pageable);
        Mockito.verify(customerMapper, Mockito.times(1)).toDto(Mockito.any(Customer.class));
    }

    @Test
    void testSearchCustomers_emptyResults() {
        CustomerSearchCriteria criteria = new CustomerSearchCriteria();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Customer> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        Mockito.when(customerRepository.searchCustomers(criteria, pageable)).thenReturn(emptyPage);

        InvalidRequestStateException exception = assertThrows(
                InvalidRequestStateException.class,
                () -> customerService.searchCustomers(criteria, 0, 10)
        );

        assertEquals("No results with the provided data. Try again.", exception.getMessage());

        Mockito.verify(customerRepository, Mockito.times(1)).searchCustomers(criteria, pageable);
        Mockito.verify(customerMapper, Mockito.never()).toDto(Mockito.any());
    }

    @Test
    void testSearchCustomers_pagination() {
        CustomerSearchCriteria criteria = new CustomerSearchCriteria();
        criteria.setName("John");
        Pageable pageable = PageRequest.of(0, 10);

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("John Doe");
        customer.setCustomerEmail("john@example.com");
        customer.setIndustry("Tech");
        customer.setCompanySize(50);
        customer.setCustomerPhoneNumber("123-456-7890");
        customer.setStatus("Active");
        customer.setAddress("NY");

        List<Customer> customers = List.of(customer);

        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setId(1L);
        customerDTO.setName("John Doe");
        customerDTO.setCustomerEmail("john@example.com");
        customerDTO.setIndustry("Tech");
        customerDTO.setCompanySize(50);
        customerDTO.setCustomerPhoneNumber("123-456-7890");
        customerDTO.setStatus("Active");
        customerDTO.setAddress("NY");

        List<CustomerDTO> customerDTOs = List.of(customerDTO);

        // Simulate more total records for pagination
        Page<Customer> customersPage = new PageImpl<>(customers, pageable, 20);

        Mockito.when(customerRepository.searchCustomers(criteria, pageable)).thenReturn(customersPage);
        Mockito.when(customerMapper.toDto(Mockito.any(Customer.class))).thenReturn(customerDTOs.get(0));

        // Act
        Map<String, Object> response = customerService.searchCustomers(criteria, 0, 10);

        // Assert
        assertEquals(20L, response.get("total_count"));
        assertEquals(2, response.get("page_count"));
        assertEquals(0, response.get("current_page"));
        assertEquals(customerDTOs, response.get("results"));

        Mockito.verify(customerRepository, Mockito.times(1)).searchCustomers(criteria, pageable);
        Mockito.verify(customerMapper, Mockito.times(1)).toDto(Mockito.any(Customer.class));
    }





}