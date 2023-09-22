package com.example.employeedirectoryproject.service;

import com.example.employeedirectoryproject.dto.AddEmployeeDto;
import com.example.employeedirectoryproject.model.Employee;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface EmployeeService {
    void saveEmployee(AddEmployeeDto addEmployeeDto);
    Employee findEmployeeByEmail(String email);
    List<Employee> getAllEmployees();
    Employee getEmployeeById(Long id);

}
