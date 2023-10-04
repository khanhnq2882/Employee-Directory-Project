package com.example.employeedirectoryproject.service;

import com.example.employeedirectoryproject.dto.SaveDepartmentDTO;
import com.example.employeedirectoryproject.model.Department;
import com.example.employeedirectoryproject.model.Employee;

import java.util.List;

public interface DepartmentService {
    void addDepartment(SaveDepartmentDTO saveDepartmentDTO);
    List<Department> getAllDepartments();
    Department getDepartmentById(Long id);
    void updateDepartment(SaveDepartmentDTO saveDepartmentDTO, Long id);
    List<Employee> getEmployeesByDepartment(Long departmentId);
    void deleteDepartment(Long id);
}
