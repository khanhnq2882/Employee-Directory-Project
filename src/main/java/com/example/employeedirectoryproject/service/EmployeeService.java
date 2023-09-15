package com.example.employeedirectoryproject.service;

import com.example.employeedirectoryproject.dto.RegistrationDto;
import com.example.employeedirectoryproject.model.Employee;

public interface EmployeeService {
    void saveEmployee(RegistrationDto registrationDto);

    Employee findEmployeeByEmail(String email);
}
