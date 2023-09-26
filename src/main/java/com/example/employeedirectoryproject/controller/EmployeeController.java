package com.example.employeedirectoryproject.controller;

import com.example.employeedirectoryproject.dto.SkillDto;
import com.example.employeedirectoryproject.dto.EditProfileDto;
import com.example.employeedirectoryproject.model.Employee;
import com.example.employeedirectoryproject.repository.DepartmentRepository;
import com.example.employeedirectoryproject.repository.EmployeeRepository;
import com.example.employeedirectoryproject.repository.PositionRepository;
import com.example.employeedirectoryproject.service.EmployeeService;
import com.example.employeedirectoryproject.service.SkillService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@Controller
public class EmployeeController {

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private SkillService skillService;

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/edit_profile")
    public String editProfile(Model model,@Valid @ModelAttribute("EditProfileDto") EditProfileDto editProfileDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee currentEmployee = employeeRepository.findByEmail(authentication.getName());
        model.addAttribute("positions", positionRepository.findAll());
        model.addAttribute("departments", departmentRepository.findAll());
        model.addAttribute("currentEmployee", currentEmployee);
        model.addAttribute("editProfileDto", editProfileDto);
        return "edit_profile";
    }

    @GetMapping("/add_new_skill")
    public String addSkillForm(Model model) {
        SkillDto skillDto = new SkillDto();
        model.addAttribute("skillDto", skillDto);
        return "add_new_skill";
    }

    @PostMapping("/add_new_skill")
    public String addNewSkill(@Valid @ModelAttribute("skillDto") SkillDto skillDto) {
        skillService.addSkill(skillDto);
        return "redirect:/edit_profile";
    }
    
    @GetMapping("/home")
    public String home(){
        return "home";
    }

}
