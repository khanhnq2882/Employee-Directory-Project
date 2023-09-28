package com.example.employeedirectoryproject.controller;

import com.example.employeedirectoryproject.dto.SaveEmployeeDTO;
import com.example.employeedirectoryproject.mapper.EmployeeMapper;
import com.example.employeedirectoryproject.model.Employee;
import com.example.employeedirectoryproject.model.Role;
import com.example.employeedirectoryproject.repository.DepartmentRepository;
import com.example.employeedirectoryproject.repository.PositionRepository;
import com.example.employeedirectoryproject.service.EmailSenderService;
import com.example.employeedirectoryproject.service.EmployeeService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@Controller
public class AdminController {
    private PositionRepository positionRepository;
    private DepartmentRepository departmentRepository;
    private EmployeeService employeeService;
    private EmailSenderService emailSenderService;

    @Autowired
    public AdminController(PositionRepository positionRepository,
                           DepartmentRepository departmentRepository,
                           EmployeeService employeeService,
                           EmailSenderService emailSenderService) {
        this.positionRepository = positionRepository;
        this.departmentRepository = departmentRepository;
        this.employeeService = employeeService;
        this.emailSenderService = emailSenderService;
    }

    @RequestMapping("/login")
    public String loginForm() {
        return "login";
    }

    @GetMapping("/save_employee")
    public String addNewEmployeeForm(Model model) {
        model.addAttribute("positions", positionRepository.findAll());
        model.addAttribute("departments", departmentRepository.findAll());
        model.addAttribute("saveEmployeeDto", new SaveEmployeeDTO());
        return "add_new_employee";
    }

    @PostMapping("/save_employee")
    public String addNewEmployee(@Valid @ModelAttribute("saveEmployeeDto") SaveEmployeeDTO saveEmployeeDto,
                                 BindingResult result,
                                 Model model) throws MessagingException{
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

    @GetMapping("/access_denied")
    public String accessDenied() {
        return "access_denied";
    }

    @GetMapping("/update_employee_form/{id}")
    public String updateEmployeeForm(@PathVariable("id") Long id, Model model) {
        Employee employee = employeeService.getEmployeeById(id);
        SaveEmployeeDTO saveEmployeeDto = EmployeeMapper.EMPLOYEE_MAPPER.mapToSaveEmployeeDto(employee);
        model.addAttribute("employee", employee);
        model.addAttribute("saveEmployeeDto", saveEmployeeDto);
        model.addAttribute("positions", positionRepository.findAll());
        model.addAttribute("departments", departmentRepository.findAll());
        return "update_employee";
    }

    @PostMapping("/update_employee/{id}")
    public String updateEmployee(@Valid @ModelAttribute("saveEmployeeDto") SaveEmployeeDTO saveEmployeeDto,
                                 @PathVariable("id") Long id){
        employeeService.updateEmployee(saveEmployeeDto, id);
        return "redirect:/list_employees";
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
