package com.example.employeedirectoryproject.controller;

import com.example.employeedirectoryproject.config.exception.NotExistEmployeeException;
import com.example.employeedirectoryproject.config.exception.NotMatchPasswordException;
import com.example.employeedirectoryproject.config.exception.WrongPasswordException;
import com.example.employeedirectoryproject.dto.CertificationDTO;
import com.example.employeedirectoryproject.dto.ChangePasswordDTO;
import com.example.employeedirectoryproject.dto.ExperienceDTO;
import com.example.employeedirectoryproject.dto.SkillDTO;
import com.example.employeedirectoryproject.model.Employee;
import com.example.employeedirectoryproject.repository.DepartmentRepository;
import com.example.employeedirectoryproject.repository.EmployeeRepository;
import com.example.employeedirectoryproject.repository.PositionRepository;
import com.example.employeedirectoryproject.service.EmployeeService;
import com.example.employeedirectoryproject.util.TbConstants;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

@Controller
public class EmployeeController {
    private PositionRepository positionRepository;
    private DepartmentRepository departmentRepository;
    private EmployeeRepository employeeRepository;
    private EmployeeService employeeService;

    @Autowired
    public EmployeeController(PositionRepository positionRepository,
                              DepartmentRepository departmentRepository,
                              EmployeeRepository employeeRepository,
                              EmployeeService employeeService) {
        this.positionRepository = positionRepository;
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
        this.employeeService = employeeService;
    }

    @GetMapping("/change_password")
    public String changePasswordForm(Model model) {
        model.addAttribute("changePasswordDTO", new ChangePasswordDTO());
        return "change_password";
    }

    @PostMapping("/change_password")
    public String changePassword(@Valid @ModelAttribute("changePasswordDTO") ChangePasswordDTO changePasswordDTO, Model model) {
        try {
            employeeService.changePassword(changePasswordDTO);
            return "redirect:/login";
        }catch (WrongPasswordException | NotMatchPasswordException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "change_password";
        }
    }

    @GetMapping("/forgot_password")
    public String forgotPasswordForm() {
        return "forgot_password";
    }

    @PostMapping("/forgot_password")
    public String forgotPassword(HttpServletRequest request, Model model) throws MessagingException {
        try{
            String email = request.getParameter("email");
            employeeService.sendMailToAdmin(email);
            return "redirect:/login";
        } catch (NotExistEmployeeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "forgot_password";
        }
    }

    @GetMapping("/employee_profile/{id}")
    public String employeeProfile(@PathVariable("id") Long id, Model model) {
        model.addAttribute("employee", employeeService.getEmployeeById(id));
        return "employee_profile";
    }

    @GetMapping("/edit_profile")
    public String editProfileForm(Model model) {
        Employee currentEmployee = employeeService.getCurrentEmployee();
        model.addAttribute("positions", positionRepository.findAll());
        model.addAttribute("departments", departmentRepository.findAll());
        model.addAttribute("currentEmployee", currentEmployee);
        return "edit_profile";
    }

    @PostMapping("/edit_profile")
    public String editProfile(HttpServletRequest request, Model model) throws Exception{
        Employee currentEmployee = employeeService.getCurrentEmployee();
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        currentEmployee.setFirstName(request.getParameter("firstName"));
        currentEmployee.setLastName(request.getParameter("lastName"));
        currentEmployee.setDateOfBirth(df.parse(request.getParameter("dateOfBirth")));
        currentEmployee.setGender(Boolean.parseBoolean(request.getParameter("gender")));
        currentEmployee.setPhoneNumber(request.getParameter("phoneNumber"));
        currentEmployee.setAddress(request.getParameter("address"));
        employeeRepository.save(currentEmployee);
        return "redirect:/employee_profile";
    }

    @GetMapping("/add_new_skill")
    public String addSkillForm(Model model) {
        SkillDTO skillDto = new SkillDTO();
        model.addAttribute("skillDto", skillDto);
        return "add_new_skill";
    }

    @PostMapping("/add_new_skill")
    public String addNewSkill(@Valid @ModelAttribute("skillDto") SkillDTO skillDto) {
        employeeService.addSkill(skillDto);
        return "redirect:/edit_profile";
    }

    @GetMapping("/add_new_certification")
    public String addCertificationForm(Model model) {
        CertificationDTO certificationDto = new CertificationDTO();
        model.addAttribute("certificationDto", certificationDto);
        return "add_new_certification";
    }

    @PostMapping("/add_new_certification")
    public String addNewCertification(@Valid @ModelAttribute("certificationDto") CertificationDTO certificationDto) {
        employeeService.addCertification(certificationDto);
        return "redirect:/edit_profile";
    }

    @GetMapping("/add_new_experience")
    public String addExperienceForm(Model model) {
        ExperienceDTO experienceDto = new ExperienceDTO();
        model.addAttribute("experienceDto", experienceDto);
        return "add_new_experience";
    }

    @PostMapping("/add_new_experience")
    public String addNewExperience(@Valid @ModelAttribute("experienceDto") ExperienceDTO experienceDto) {
        employeeService.addExperience(experienceDto);
        return "redirect:/edit_profile";
    }
    
    @GetMapping("/home")
    public String home(Model model){
        Employee currentEmployee = employeeService.getCurrentEmployee();
        if (currentEmployee.getRoles().stream().filter(role -> role.getName().equals(TbConstants.Roles.ADMIN)).findAny().isPresent()) {
            model.addAttribute("currentEmployee", currentEmployee);
            return "admin";
        }
        model.addAttribute("currentEmployee", currentEmployee);
        return "employee";
    }


}
