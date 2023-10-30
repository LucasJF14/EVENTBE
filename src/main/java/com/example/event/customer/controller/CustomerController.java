package com.example.event.customer.controller;

import com.example.event.customer.service.CustomerDetailDTO;
import com.example.event.customer.service.CustomerRequestDTO;
import com.example.event.customer.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService){
        this.customerService = customerService;
    }

    @GetMapping("/api/customers")
    public List<CustomerDetailDTO> searchCustomer(){
        System.out.println("Search Customer.");

        return customerService.getAllCustomers();
    }

    @GetMapping("/api/customers/{customerId}")
    public CustomerDetailDTO getCustomerById(@PathVariable Long customerId){
        System.out.println("Search Customer.");

        return customerService.getCustomerById(customerId);
    }

    @PostMapping("/api/customers")
    public Long createCustomer(@Valid @RequestBody CustomerRequestDTO customerRequestDTO){
        System.out.println("Create Customer.");

        return customerService.createCustomer(customerRequestDTO);
    }

    @PutMapping("/api/customers/{customerId}")
    public void updateCustomer(@Valid @PathVariable Long customerId, @RequestBody CustomerRequestDTO customerRequestDTO){
        System.out.println("Update Customer.");

        customerService.updateCustomer(customerRequestDTO, customerId);
    }

    @DeleteMapping("/api/customers/{customerId}")
    public void deleteCustomer(@PathVariable Long customerId){
        System.out.println("Delete Customer.");

        customerService.deleteCustomer(customerId);
    }
}
