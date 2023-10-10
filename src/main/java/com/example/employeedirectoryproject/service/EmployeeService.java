package com.example.employeedirectoryproject.service;

import com.example.employeedirectoryproject.dto.*;
import com.example.employeedirectoryproject.model.Certification;
import com.example.employeedirectoryproject.model.Employee;
import com.example.employeedirectoryproject.model.Experience;
import com.example.employeedirectoryproject.model.Skill;
import jakarta.mail.MessagingException;
import java.util.List;

public interface EmployeeService {
    void changePassword(ChangePasswordDTO changePasswordDTO);
    void saveEmployee(SaveEmployeeDTO saveEmployeeDto) throws MessagingException;
    Employee findEmployeeByEmail(String email);
    List<Employee> getAllEmployees();
    Employee getEmployeeById(Long id);
    void updateEmployee(SaveEmployeeDTO saveEmployeeDto, Long id);
    void deleteEmployeeRole(Long employeeId, Long roleId);
    void deleteEmployee(Long id);
    Employee getCurrentEmployee();
    List<Employee> searchEmployees(String search);
    void addSkill(SkillDTO skillDto);
    void addExperience(ExperienceDTO experienceDto);
    void addCertification(CertificationDTO certificationDto);
    void sendMailToAdmin(String email) throws MessagingException ;
    List<Skill> getEmployeeSkills(Long employeeId);
    List<Certification> getEmployeeCertifications(Long employeeId);
    List<Experience> getEmployeeExperiences(Long employeeId);

}
