package com.customer.spring.repository;

import com.customer.spring.entity.Customer;
import com.customer.spring.entity.CustomerSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByCustomerEmail(String customerEmail);
    Optional<Customer> findByCustomerPhoneNumber(String customerPhoneNumber);

    @Query("SELECT c FROM Customer c WHERE " +
            "(:#{#criteria.name} IS NULL OR c.name LIKE %:#{#criteria.name}%) AND " +
            "(:#{#criteria.customerEmail} IS NULL OR c.customerEmail LIKE %:#{#criteria.customerEmail}%) AND " +
            "(:#{#criteria.industry} IS NULL OR c.industry = :#{#criteria.industry}) AND " +
            "(:#{#criteria.companySize} IS NULL OR c.companySize = :#{#criteria.companySize}) AND " +
            "(:#{#criteria.customerPhoneNumber} IS NULL OR c.customerPhoneNumber LIKE %:#{#criteria.customerPhoneNumber}%) AND " +
            "(:#{#criteria.status} IS NULL OR c.status = :#{#criteria.status}) AND " +
            "(:#{#criteria.address} IS NULL OR c.address LIKE %:#{#criteria.address}%)")
    Page<Customer> searchCustomers(@Param("criteria") CustomerSearchCriteria criteria, Pageable pageable);


}
