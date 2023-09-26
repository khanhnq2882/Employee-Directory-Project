package com.example.employeedirectoryproject.controller;

import com.example.employeedirectoryproject.dto.SaveEmployeeDto;
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
import java.util.*;
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

    @GetMapping("/save_employee")
    public String addNewEmployeeForm(Model model) {
        SaveEmployeeDto saveEmployeeDto = new SaveEmployeeDto();
        model.addAttribute("positions", positionRepository.findAll());
        model.addAttribute("departments", departmentRepository.findAll());
        model.addAttribute("saveEmployeeDto", saveEmployeeDto);
        return "add_new_employee";
    }

    @PostMapping("/save_employee")
    public String addNewEmployee(@Valid @ModelAttribute("saveEmployeeDto") SaveEmployeeDto saveEmployeeDto, BindingResult result, Model model) {
        Employee existingEmployee = employeeService.findEmployeeByEmail(saveEmployeeDto.getEmail());
        if (existingEmployee != null) {
            result.rejectValue("email", null, "Employee email is already existed.");
        }
        if (result.hasErrors()) {
            model.addAttribute("saveEmployeeDto", saveEmployeeDto);
            return "add_new_employee";
        }
        employeeService.saveEmployee(saveEmployeeDto);
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

    @GetMapping("/update_employee_form/{id}")
    public String updateEmployeeForm(@PathVariable("id") Long id, Model model) {
        Employee employee = employeeService.getEmployeeById(id);
        SaveEmployeeDto saveEmployeeDto = SaveEmployeeDto.builder()
                .email(employee.getEmail())
                .password(employee.getPassword())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .dateOfBirth(employee.getDateOfBirth())
                .phoneNumber(employee.getPhoneNumber())
                .address(employee.getAddress())
                .coefficientsSalary(employee.getCoefficientsSalary())
                .gender(employee.getGender())
                .status(employee.getStatus())
                .startWork(employee.getStartWork())
                .endWork(employee.getEndWork())
                .department(employee.getDepartment())
                .position(employee.getPosition())
                .build();
        model.addAttribute("saveEmployeeDto", saveEmployeeDto);
        model.addAttribute("employeeId", employee.getEmployeeId());
        model.addAttribute("positions", positionRepository.findAll());
        model.addAttribute("departments", departmentRepository.findAll());
        return "update_employee";
    }

    @PostMapping("/update_employee/{id}")
    public String updateEmployee(@Valid @ModelAttribute("saveEmployeeDto") SaveEmployeeDto saveEmployeeDto, @PathVariable("id") Long id) {
        employeeService.updateEmployee(saveEmployeeDto, id);
        return "redirect:/list_employees";
    }

    @GetMapping("/email_form/{id}")
    public String showEmailForm(Model model, @PathVariable("id") Long id) {
        Employee employee = employeeService.getEmployeeById(id);
        model.addAttribute("employee", employee);
        return "email_employee";
    }

    @GetMapping("/delete_employee/{id}")
    public String deleteEmployee(@PathVariable("id") Long id) {
        Employee employee = employeeService.getEmployeeById(id);
        for (Role role: employee.getRoles()) {
            employeeService.deleteEmployeeRole(id, role.getRoleId());
        }
        employeeService.deleteEmployee(id);
        return "redirect:/list_employees";
    }

    @PostMapping("/send_mail_employee")
    public String sendMailEmployee(HttpServletRequest request) throws MessagingException {
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
        return "redirect:/list_employees";
    }

    @GetMapping("/list_employees")
    public String getListEmployees(HttpServletRequest request, Model model) {
        String searchText = request.getParameter("searchText");
        List<Employee> employees = new ArrayList<>();
        if (Objects.isNull(searchText)) {
            employees = employeeService.getAllEmployees();
        } else {
            employees = employeeService.searchEmployees(searchText);
        }
        model.addAttribute("employees", employees);
        return "list_employees";
    }

}
