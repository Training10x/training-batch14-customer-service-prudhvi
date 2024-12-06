package com.customer.spring.repository;

import com.customer.spring.entity.Customer;
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
            "(:name IS NULL OR c.name LIKE %:name%) AND " +
            "(:customerEmail IS NULL OR c.customerEmail LIKE %:customerEmail%) AND " +
            "(:industry IS NULL OR c.industry = :industry) AND " +
            "(:companySize IS NULL OR c.companySize = :companySize) AND " +
            "(:customerPhoneNumber IS NULL OR c.customerPhoneNumber LIKE %:customerPhoneNumber%) AND " +
            "(:status IS NULL OR c.status = :status) AND " +
            "(:address IS NULL OR c.address LIKE %:address%)")
    Page<Customer> searchCustomers(
            @Param("name") String name,
            @Param("customerEmail") String customerEmail,
            @Param("industry") String industry,
            @Param("companySize") Integer companySize,
            @Param("customerPhoneNumber") String customerPhoneNumber,
            @Param("status") String status,
            @Param("address") String address,
            Pageable pageable
    );

}
