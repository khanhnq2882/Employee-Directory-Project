package com.example.employeedirectoryproject.service.serviceImpl;

import com.example.employeedirectoryproject.dto.SaveEmployeeDto;
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
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void saveEmployee(SaveEmployeeDto saveEmployeeDto) {
        Role role = roleRepository.findByName(TbConstants.Roles.EMPLOYEE);
        if (role == null) {
            role = roleRepository.save(new Role(TbConstants.Roles.EMPLOYEE));
        }
        Employee employee = new Employee(saveEmployeeDto.getFirstName(),
                saveEmployeeDto.getLastName(),
                saveEmployeeDto.getEmail(),
                passwordEncoder.encode(saveEmployeeDto.getPassword()),
                saveEmployeeDto.getGender(),
                saveEmployeeDto.getDateOfBirth(),
                saveEmployeeDto.getPhoneNumber(),
                saveEmployeeDto.getAddress(),
                saveEmployeeDto.getStartWork(),
                saveEmployeeDto.getEndWork(),
                saveEmployeeDto.getCoefficientsSalary(),
                saveEmployeeDto.getStatus(),
                saveEmployeeDto.getDepartment(),
                saveEmployeeDto.getPosition(),
                Arrays.asList(role));
        employeeRepository.save(employee);
    }

    @Override
    public Employee findEmployeeByEmail(String email) {
        return employeeRepository.findByEmail(email);
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id).orElse(null);
    }

    @Override
    public void updateEmployee(SaveEmployeeDto saveEmployeeDto, Long id) {
        Employee employee = employeeRepository.findById(id).orElse(null);
        if (employee.getRoles().stream().filter(role -> role.getName().equals("ROLE_EMPLOYEE")).findAny().isPresent() == true) {
            employee = mapToEntity(employee, saveEmployeeDto);
            employeeRepository.save(employee);
        }
    }

    @Override
    public void deleteEmployeeRole(Long employeeId, Long roleId) {
        employeeRepository.deleteEmployeeRole(employeeId, roleId);
    }

    @Override
    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }

    @Override
    public List<Employee> searchEmployees(String search) {
        return employeeRepository.search(search);
    }

    public Employee mapToEntity(Employee employee, SaveEmployeeDto saveEmployeeDto) {
        employee = Employee.builder()
                .firstName(saveEmployeeDto.getFirstName())
                .lastName(saveEmployeeDto.getLastName())
                .email(saveEmployeeDto.getEmail())
                .password(passwordEncoder.encode(saveEmployeeDto.getPassword()))
                .gender(saveEmployeeDto.getGender())
                .dateOfBirth(saveEmployeeDto.getDateOfBirth())
                .phoneNumber(saveEmployeeDto.getPhoneNumber())
                .address(saveEmployeeDto.getAddress())
                .startWork(saveEmployeeDto.getStartWork())
                .endWork(saveEmployeeDto.getEndWork())
                .coefficientsSalary(saveEmployeeDto.getCoefficientsSalary())
                .status(saveEmployeeDto.getStatus())
                .department(saveEmployeeDto.getDepartment())
                .position(saveEmployeeDto.getPosition())
                .build();
        return employee;
    }


}
