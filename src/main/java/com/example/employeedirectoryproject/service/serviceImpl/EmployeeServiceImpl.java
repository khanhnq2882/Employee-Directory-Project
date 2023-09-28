package com.example.employeedirectoryproject.service.serviceImpl;

import com.example.employeedirectoryproject.config.exception.NotMatchPasswordException;
import com.example.employeedirectoryproject.config.exception.WrongPasswordException;
import com.example.employeedirectoryproject.dto.*;
import com.example.employeedirectoryproject.mapper.CertificationMapper;
import com.example.employeedirectoryproject.mapper.EmployeeMapper;
import com.example.employeedirectoryproject.mapper.ExperienceMapper;
import com.example.employeedirectoryproject.mapper.SkillMapper;
import com.example.employeedirectoryproject.model.*;
import com.example.employeedirectoryproject.repository.*;
import com.example.employeedirectoryproject.service.EmailSenderService;
import com.example.employeedirectoryproject.service.EmployeeService;
import com.example.employeedirectoryproject.util.TbConstants;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private EmployeeRepository employeeRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private SkillRepository skillRepository;
    private ExperienceRepository experienceRepository;
    private CertificationRepository certificationRepository;
    private EmailSenderService emailSenderService;
    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository,
                               RoleRepository roleRepository,
                               PasswordEncoder passwordEncoder,
                               SkillRepository skillRepository,
                               ExperienceRepository experienceRepository,
                               CertificationRepository certificationRepository,
                               EmailSenderService emailSenderService) {
        this.employeeRepository = employeeRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.skillRepository = skillRepository;
        this.experienceRepository = experienceRepository;
        this.certificationRepository = certificationRepository;
        this.emailSenderService = emailSenderService;
    }

    @Override
    public void changePassword(ChangePasswordDTO changePasswordDTO) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        if (bCryptPasswordEncoder.matches(changePasswordDTO.getOldPassword(), getCurrentEmployee().getPassword())) {
            if (changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmPassword())) {
                getCurrentEmployee().setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
                employeeRepository.save(getCurrentEmployee());
            } else {
                throw new NotMatchPasswordException("New password and confirm password is not match. Try again!");
            }
        } else {
            throw new WrongPasswordException("Old password is not correct. Try again!");
        }
    }

    @Override
    public void saveEmployee(SaveEmployeeDTO saveEmployeeDto) throws MessagingException{
        Role role = roleRepository.findByName(TbConstants.Roles.EMPLOYEE);
        if (Objects.isNull(role)) {
            role = roleRepository.save(new Role(TbConstants.Roles.EMPLOYEE));
        }
        Employee employee = Employee.builder()
                .firstName(saveEmployeeDto.getFirstName())
                .lastName(saveEmployeeDto.getLastName())
                .email(saveEmployeeDto.getEmail())
                .password(randomPassword())
                .dateOfBirth(saveEmployeeDto.getDateOfBirth())
                .phoneNumber(saveEmployeeDto.getPhoneNumber())
                .address(saveEmployeeDto.getAddress())
                .gender(saveEmployeeDto.getGender())
                .startWork(saveEmployeeDto.getStartWork())
                .endWork(saveEmployeeDto.getEndWork())
                .coefficientsSalary(saveEmployeeDto.getCoefficientsSalary())
                .department(saveEmployeeDto.getDepartment())
                .position(saveEmployeeDto.getPosition())
                .status(saveEmployeeDto.getStatus())
                .roles(Arrays.asList(role))
                .build();
        saveEmployeeDto.setPassword(employee.getPassword());
        sendEmail(saveEmployeeDto);
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        employeeRepository.save(employee);
    }

    @Override
    public Employee findEmployeeByEmail(String email) {
        return employeeRepository.findByEmail(email);
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.getAllEmployees();
    }

    @Override
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id).orElse(null);
    }

    @Override
    public void updateEmployee(SaveEmployeeDTO saveEmployeeDto, Long id) {
        Employee employee = employeeRepository.findById(id).orElse(null);
        EmployeeMapper.EMPLOYEE_MAPPER.mapToUpdateEmployee(employee, saveEmployeeDto);
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
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
        Skill skill = SkillMapper.SKILL_MAPPER.mapToSkill(skillDto);
        skill.setEmployees(Arrays.asList(currentEmployee));
        currentEmployee.getSkills().add(skill);
        skillRepository.save(skill);
    }

    @Override
    public void addExperience(ExperienceDTO experienceDto) {
        Employee currentEmployee = getCurrentEmployee();
        Experience experience = ExperienceMapper.EXPERIENCE_MAPPER.mapToExperience(experienceDto);
        experience.setEmployee(currentEmployee);
        experienceRepository.save(experience);
    }

    @Override
    public void addCertification(CertificationDTO certificationDto) {
        Employee currentEmployee = getCurrentEmployee();
        Certification certification = CertificationMapper.CERTIFICATION_MAPPER.mapToCertification(certificationDto);
        certification.setEmployee(currentEmployee);
        certificationRepository.save(certification);
    }

    public String randomPassword() {
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        return IntStream.range(0, 8)
                .map(i -> random.nextInt(chars.length()))
                .mapToObj(randomIndex -> String.valueOf(chars.charAt(randomIndex)))
                .collect(Collectors.joining());
    }

    public void sendEmail(SaveEmployeeDTO saveEmployeeDTO) throws MessagingException {
        EmailDTO emailDto = new EmailDTO();
        emailDto.setFrom("quockhanhnguyen2882@gmail.com");
        emailDto.setTo(saveEmployeeDTO.getPersonalEmail());
        emailDto.setSubject("Company send information for new employee "+saveEmployeeDTO.getFirstName()+" "+saveEmployeeDTO.getLastName());
        Map<String, Object> properties = new HashMap<>();
        properties.put("email", saveEmployeeDTO.getEmail());
        properties.put("password", saveEmployeeDTO.getPassword());
        properties.put("fullName", saveEmployeeDTO.getFirstName()+" "+saveEmployeeDTO.getLastName());
        emailDto.setProperties(properties);
        emailDto.setTemplate("email_content.html");
        emailSenderService.sendHtmlMessage(emailDto);
    }


}
