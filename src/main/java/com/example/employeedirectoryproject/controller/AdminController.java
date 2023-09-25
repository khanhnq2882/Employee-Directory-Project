package com.example.employeedirectoryproject.controller;

import com.example.employeedirectoryproject.dto.AddEmployeeDto;
import com.example.employeedirectoryproject.dto.EmailDto;
import com.example.employeedirectoryproject.model.Employee;
import com.example.employeedirectoryproject.model.Role;
import com.example.employeedirectoryproject.repository.DepartmentRepository;
import com.example.employeedirectoryproject.repository.EmployeeRepository;
import com.example.employeedirectoryproject.repository.PositionRepository;
import com.example.employeedirectoryproject.service.EmailSenderService;
import com.example.employeedirectoryproject.service.EmployeeService;
import com.example.employeedirectoryproject.util.TbConstants;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@Controller
public class AdminController {

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmailSenderService emailSenderService;


    @RequestMapping("/login")
    public String loginForm() {
        return "login";
    }

    @GetMapping("/add_new_employee")
    public String registrationForm(Model model) {
        AddEmployeeDto addEmployeeDto = new AddEmployeeDto();
        model.addAttribute("positions", positionRepository.findAll());
        model.addAttribute("departments", departmentRepository.findAll());
        model.addAttribute("addEmployeeDto", addEmployeeDto);
        return "add_new_employee";
    }

    @PostMapping("/add_new_employee")
    public String registration(@Valid @ModelAttribute("addEmployeeDto") AddEmployeeDto addEmployeeDto, BindingResult result, Model model) {
        Employee existingEmployee = employeeService.findEmployeeByEmail(addEmployeeDto.getEmail());
        if (existingEmployee != null)
            result.rejectValue("email", null, "Employee email is already existed.");
        if (result.hasErrors()) {
            model.addAttribute("addEmployeeDto", addEmployeeDto);
            return "add_new_employee";
        }
        employeeService.saveEmployee(addEmployeeDto);
        return "redirect:/list_employees";
    }

    @GetMapping("/admin")
    public String admin(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee currentEmployee = employeeRepository.findByEmail(authentication.getName());
        for (Role role: currentEmployee.getRoles()) {
            if (!role.getName().equals(TbConstants.Roles.ADMIN)) {
                return "redirect:/access_denied";
            }
        }
        return "admin";
    }

    @GetMapping("/access_denied")
    public String accessDenied() {
        return "access_denied";
    }

    @GetMapping("/update_employee/{id}")
    public String updateEmployee(@PathVariable("id") Long id, Model model) {
        return "update_employee";
    }

    @GetMapping("/show_email_form/{id}")
    public String showEmailForm(Model model, @PathVariable("id") Long id) {
        Employee employee = employeeService.getEmployeeById(id);
        model.addAttribute("employee", employee);
        return "email_employee";
    }

    @PostMapping("/send_mail_employee")
    public void sendMailEmployee(HttpServletRequest request) throws MessagingException {
        String personalEmail = request.getParameter("personalEmail");
        String email = request.getParameter("email");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        EmailDto emailDto = new EmailDto();
        emailDto.setFrom("quockhanhnguyen2882@gmail.com");
        emailDto.setTo(personalEmail);
        emailDto.setSubject("Company send information for new employee "+firstName+" "+lastName);
        Map<String, Object> properties = new HashMap<>();
        properties.put("email", email);
        properties.put("password", "123456");
        properties.put("fullName", firstName +" "+lastName);
        emailDto.setProperties(properties);
        emailDto.setTemplate("email_content.html");
        emailSenderService.sendHtmlMessage(emailDto);
    }

}
