package com.example.event.customer.service;

import jakarta.validation.constraints.NotBlank;

public class CustomerRequestDTO {
    @NotBlank(message = "Name must no be empty.")
    String firstName;

    @NotBlank(message = "Surname must no be empty.")
    String lastName;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
