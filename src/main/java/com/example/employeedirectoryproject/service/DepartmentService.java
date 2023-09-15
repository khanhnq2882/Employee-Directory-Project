package com.example.employeedirectoryproject.service;

import com.example.employeedirectoryproject.model.Department;

import java.util.List;

public interface DepartmentService {
    List<Department> getAllDepartments();
    void addDepartment(Department department);
    Department updateDepartment(Long id);
    void deleteDepartment(Long id);

}
