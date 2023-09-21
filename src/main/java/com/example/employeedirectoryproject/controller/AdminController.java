package com.example.employeedirectoryproject.controller;

import com.example.employeedirectoryproject.dto.AddEmployeeDto;
import com.example.employeedirectoryproject.model.Employee;
import com.example.employeedirectoryproject.repository.DepartmentRepository;
import com.example.employeedirectoryproject.repository.PositionRepository;
import com.example.employeedirectoryproject.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AdminController {

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeService employeeService;

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
    public String admin(Model model){
        return "admin";
    }


}
