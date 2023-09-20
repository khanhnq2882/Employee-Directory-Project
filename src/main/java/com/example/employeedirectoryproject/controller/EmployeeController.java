package com.example.employeedirectoryproject.controller;

import com.example.employeedirectoryproject.model.Employee;
import com.example.employeedirectoryproject.repository.DepartmentRepository;
import com.example.employeedirectoryproject.repository.EmployeeRepository;
import com.example.employeedirectoryproject.repository.PositionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/edit_profile")
public class EmployeeController {

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping("/")
    public String editProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee currentEmployee = employeeRepository.findByEmail(authentication.getName());
        model.addAttribute("positions", positionRepository.findAll());
        model.addAttribute("departments", departmentRepository.findAll());
        model.addAttribute("currentEmployee", currentEmployee);
        return "edit_profile";
    }



}
