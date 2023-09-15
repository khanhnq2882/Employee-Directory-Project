package com.example.employeedirectoryproject.service.serviceImpl;

import com.example.employeedirectoryproject.dto.RegistrationDto;
import com.example.employeedirectoryproject.model.Employee;
import com.example.employeedirectoryproject.model.Role;
import com.example.employeedirectoryproject.repository.EmployeeRepository;
import com.example.employeedirectoryproject.repository.RoleRepository;
import com.example.employeedirectoryproject.service.EmployeeService;
import com.example.employeedirectoryproject.util.TbConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void saveEmployee(RegistrationDto registrationDto) {
        Role role = roleRepository.findByName(TbConstants.Roles.EMPLOYEE);
        if (role == null) {
            role = roleRepository.save(new Role(TbConstants.Roles.EMPLOYEE));
        }
        Employee employee = new Employee(registrationDto.getFirstName(),
                registrationDto.getLastName(),
                registrationDto.getEmail(),
                passwordEncoder.encode(registrationDto.getPassword()),
                registrationDto.getPhoneNumber(),
                Arrays.asList(role));
        employeeRepository.save(employee);
    }

    @Override
    public Employee findEmployeeByEmail(String email) {
        return employeeRepository.findByEmail(email);
    }

}
