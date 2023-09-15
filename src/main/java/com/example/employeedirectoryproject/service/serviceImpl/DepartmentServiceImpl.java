package com.example.employeedirectoryproject.service.serviceImpl;

import com.example.employeedirectoryproject.model.Department;
import com.example.employeedirectoryproject.repository.DepartmentRepository;
import com.example.employeedirectoryproject.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    @Override
    public void addDepartment(Department department) {
        departmentRepository.save(department);
    }

    @Override
    public Department updateDepartment(Long id) {
        Department existDepartment = departmentRepository.findAll().stream().filter(department -> department.getId() == id).findAny().orElse(null);
        if (existDepartment != null) {

        }
        return null;

    }

    @Override
    public void deleteDepartment(Long id) {
        departmentRepository.deleteById(id);
    }

}
