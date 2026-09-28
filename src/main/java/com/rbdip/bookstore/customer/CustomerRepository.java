package com.rbdip.bookstore.customer;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("select c from Customer c"
            + " where c.fullName = :fullName"
            + " and c.address is not distinct from :address"
            + " and c.phone is not distinct from :phone")
    Optional<Customer> findMatching(String fullName, String address, String phone);
}
