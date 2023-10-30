package com.example.event.customer.persistance.repository;

import com.example.event.customer.persistance.entity.CustomerEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends CrudRepository<CustomerEntity, Long> {
    CustomerEntity getCustomerEntityById(Long eventId);
}
