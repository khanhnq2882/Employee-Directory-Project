package com.example.employeedirectoryproject.service.serviceImpl;

import com.example.employeedirectoryproject.dto.*;
import com.example.employeedirectoryproject.model.*;
import com.example.employeedirectoryproject.repository.*;
import com.example.employeedirectoryproject.service.EmployeeService;
import com.example.employeedirectoryproject.util.TbConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private ExperienceRepository experienceRepository;

    @Autowired
    private CertificationRepository certificationRepository;

    @Override
    public void saveEmployee(SaveEmployeeDTO saveEmployeeDto) {
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
    public void updateEmployee(SaveEmployeeDTO saveEmployeeDto, Long id) {
        Employee employee = employeeRepository.findById(id).orElse(null);
        mapToEntity(employee, saveEmployeeDto);
        employeeRepository.save(employee);
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
    public Employee getCurrentEmployee() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee currentEmployee = employeeRepository.findByEmail(authentication.getName());
        return currentEmployee;
    }

    @Override
    public List<Employee> searchEmployees(String search) {
        return employeeRepository.search(search);
    }

    @Override
    public void addSkill(SkillDTO skillDto) {
        Employee currentEmployee = getCurrentEmployee();
        Skill skill = Skill.builder()
                .skillName(skillDto.getSkillName())
                .level(skillDto.getLevel())
                .description(skillDto.getDescription())
                .employees(Arrays.asList(currentEmployee))
                .build();
        currentEmployee.getSkills().add(skill);
        skillRepository.save(skill);
    }

    @Override
    public void addExperience(ExperienceDTO experienceDto) {
        Employee currentEmployee = getCurrentEmployee();
        Experience experience = Experience.builder()
                .companyName(experienceDto.getCompanyName())
                .name(experienceDto.getProjectName())
                .language(experienceDto.getLanguage())
                .framework(experienceDto.getFramework())
                .startWork(experienceDto.getStartWork())
                .endWork(experienceDto.getEndWork())
                .description(experienceDto.getDescription())
                .employee(currentEmployee)
                .build();
        experienceRepository.save(experience);
    }

    @Override
    public void addCertification(CertificationDTO certificationDto) {
        Employee currentEmployee = getCurrentEmployee();
        Certification certification = Certification.builder()
                .certificationName(certificationDto.getCertificationName())
                .issuedDate(certificationDto.getIssuedDate())
                .expiredDate(certificationDto.getExpiredDate())
                .description(certificationDto.getDescription())
                .employee(currentEmployee)
                .build();
        certificationRepository.save(certification);
    }

    private void mapToEntity(Employee employee, SaveEmployeeDTO saveEmployeeDto) {
        employee.setFirstName(saveEmployeeDto.getFirstName());
        employee.setLastName(saveEmployeeDto.getLastName());
        employee.setEmail(saveEmployeeDto.getEmail());
        employee.setPassword(passwordEncoder.encode(saveEmployeeDto.getPassword()));
        employee.setGender(saveEmployeeDto.getGender());
        employee.setDateOfBirth(saveEmployeeDto.getDateOfBirth());
        employee.setPhoneNumber(saveEmployeeDto.getPhoneNumber());
        employee.setAddress(saveEmployeeDto.getAddress());
        employee.setStartWork(saveEmployeeDto.getStartWork());
        employee.setEndWork(saveEmployeeDto.getEndWork());
        employee.setCoefficientsSalary(saveEmployeeDto.getCoefficientsSalary());
        employee.setStatus(saveEmployeeDto.getStatus());
        employee.setDepartment(saveEmployeeDto.getDepartment());
        employee.setPosition(saveEmployeeDto.getPosition());
    }

}
