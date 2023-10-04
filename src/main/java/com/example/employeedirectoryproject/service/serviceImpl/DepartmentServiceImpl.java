package com.example.employeedirectoryproject.service.serviceImpl;

import com.example.employeedirectoryproject.dto.SaveDepartmentDTO;
import com.example.employeedirectoryproject.model.Department;
import com.example.employeedirectoryproject.model.Employee;
import com.example.employeedirectoryproject.repository.DepartmentRepository;
import com.example.employeedirectoryproject.service.DepartmentService;
import com.example.employeedirectoryproject.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private DepartmentRepository departmentRepository;

    private EmployeeService employeeService;

    @Autowired
    public DepartmentServiceImpl(DepartmentRepository departmentRepository, EmployeeService employeeService) {
        this.departmentRepository = departmentRepository;
        this.employeeService = employeeService;
    }

    @Override
    public void addDepartment(SaveDepartmentDTO saveDepartmentDTO) {
        Employee currentEmployee = employeeService.getCurrentEmployee();
        Department department = Department.builder()
                .departmentName(saveDepartmentDTO.getDepartmentName())
                .description(saveDepartmentDTO.getDescription())
                .createdBy(currentEmployee.getFirstName()+" "+currentEmployee.getLastName())
                .build();
        departmentRepository.save(department);
    }

    @Override
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    @Override
    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id).orElse(null);
    }

    @Override
    public void updateDepartment(SaveDepartmentDTO saveDepartmentDTO, Long id) {
        Employee currentEmployee = employeeService.getCurrentEmployee();
        Department department = getDepartmentById(id);
        department.setDepartmentName(saveDepartmentDTO.getDepartmentName());
        department.setDescription(saveDepartmentDTO.getDescription());
        department.setUpdatedBy(currentEmployee.getFirstName()+" "+currentEmployee.getLastName());
        departmentRepository.save(department);
    }

    @Override
    public List<Employee> getEmployeesByDepartment(Long departmentId) {
        return departmentRepository.getEmployeesByDepartment(departmentId);
    }

    @Override
    public void deleteDepartment(Long id) {
        List<Employee> employees = departmentRepository.getEmployeesByDepartment(id);
        if (employees.isEmpty()) {
            departmentRepository.deleteById(id);
        }
    }

}
