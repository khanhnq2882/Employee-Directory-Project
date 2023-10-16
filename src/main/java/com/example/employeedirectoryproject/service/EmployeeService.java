package com.example.employeedirectoryproject.service;

import com.example.employeedirectoryproject.dto.*;
import com.example.employeedirectoryproject.model.Certification;
import com.example.employeedirectoryproject.model.Employee;
import com.example.employeedirectoryproject.model.Experience;
import com.example.employeedirectoryproject.model.Skill;
import jakarta.mail.MessagingException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EmployeeService {
    void changePassword(ChangePasswordDTO changePasswordDTO);
    void resetPassword(String email) throws MessagingException ;
    void saveEmployee(SaveEmployeeDTO saveEmployeeDto) throws MessagingException;
    Employee findEmployeeByEmail(String email);
    Employee findByPhoneNumber(String phoneNumber);
    List<Employee> listEmployees();
    Page<Employee> getAllEmployees(Pageable pageable);
    Employee getEmployeeById(Long id);
    void updateEmployee(SaveEmployeeDTO saveEmployeeDto, Long id);
    void deleteEmployeeRole(Long employeeId, Long roleId);
    void deleteEmployee(Long id);
    Employee getCurrentEmployee();
    Page<Employee> searchEmployees(String searchText, Pageable pageable);
    void addSkill(SkillDTO skillDto);
    void addExperience(ExperienceDTO experienceDto);
    void addCertification(CertificationDTO certificationDto);
    void sendMailToAdmin(String email) throws MessagingException ;
    List<Skill> getEmployeeSkills(Long employeeId);
    List<Certification> getEmployeeCertifications(Long employeeId);
    List<Experience> getEmployeeExperiences(Long employeeId);
    Page<Employee> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection, String searchText);
}
