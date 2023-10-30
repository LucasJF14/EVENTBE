package com.example.event.customer.service;

import com.example.event.customer.persistance.entity.CustomerEntity;
import com.example.event.customer.persistance.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<CustomerDetailDTO> getAllCustomers() {
        return mapToDtoList(customerRepository.findAll());
    }

    public CustomerDetailDTO getCustomerById(Long eventId) {
        return mapToDto(customerRepository.getCustomerEntityById(eventId));
    }

    public Long createCustomer(CustomerRequestDTO customerRequestDTO) {
        CustomerEntity entity = mapToEntity(customerRequestDTO);

        return customerRepository.save(entity).getId();
    }

    @Transactional
    public void updateCustomer(CustomerRequestDTO customerRequestDTO, Long eventId) {
        CustomerEntity entity = customerRepository.getCustomerEntityById(eventId);

        if(!Strings.isEmpty(customerRequestDTO.getFirstName())){
            entity.setFirstName(customerRequestDTO.getFirstName());
        }

        if(!Strings.isEmpty(customerRequestDTO.getLastName())){
            entity.setLastName(customerRequestDTO.getLastName());
        }

        customerRepository.save(entity);
    }

    @Transactional
    public void deleteCustomer(Long customerId) {
        CustomerEntity entity = customerRepository.getCustomerEntityById(customerId);

        customerRepository.deleteById(entity.getId());
    }

    private CustomerEntity mapToEntity(CustomerRequestDTO customerRequestDTO) {
        CustomerEntity entity = new CustomerEntity();

        entity.setFirstName(customerRequestDTO.getFirstName());
        entity.setLastName(customerRequestDTO.getLastName());

        return entity;
    }

    private List<CustomerDetailDTO> mapToDtoList(Iterable<CustomerEntity> entities) {
        List<CustomerDetailDTO> events = new ArrayList<>();

        entities.forEach(customerEntity -> {
            CustomerDetailDTO dto = mapToDto(customerEntity);
            events.add(dto);
        });

        return events;
    }

    private CustomerDetailDTO mapToDto(CustomerEntity customerEntity) {
        CustomerDetailDTO dto = new CustomerDetailDTO();

        dto.setId(customerEntity.getId());
        dto.setFirstName(customerEntity.getFirstName());
        dto.setLastName(customerEntity.getLastName());

        return dto;
    }

}
