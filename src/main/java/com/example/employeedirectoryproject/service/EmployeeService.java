package com.example.employeedirectoryproject.service;

import com.example.employeedirectoryproject.dto.SaveEmployeeDto;
import com.example.employeedirectoryproject.model.Employee;

import java.util.List;

public interface EmployeeService {
    void saveEmployee(SaveEmployeeDto saveEmployeeDto);
    Employee findEmployeeByEmail(String email);
    List<Employee> getAllEmployees();
    Employee getEmployeeById(Long id);
    void updateEmployee(SaveEmployeeDto saveEmployeeDto, Long id);
    void deleteEmployeeRole(Long employeeId, Long roleId);
    void deleteEmployee(Long id);
    List<Employee> searchEmployees(String search);

}
