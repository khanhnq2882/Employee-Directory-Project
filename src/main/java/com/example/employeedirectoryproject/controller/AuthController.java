package com.example.employeedirectoryproject.controller;

import com.example.employeedirectoryproject.dto.RegistrationDto;
import com.example.employeedirectoryproject.model.Employee;
import com.example.employeedirectoryproject.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/registration")
    public String registrationForm(Model model) {
        RegistrationDto registrationDto = new RegistrationDto();
        model.addAttribute("registrationDto", registrationDto);
        return "registration";
    }

    @PostMapping("/registration")
    public String registration(
            @Valid @ModelAttribute("user") RegistrationDto registrationDto,
            BindingResult result,
            Model model) {
        Employee existingEmployee = employeeService.findEmployeeByEmail(registrationDto.getEmail());
        if (existingEmployee != null)
            result.rejectValue("email", null,
                    "User already registered !!!");

        if (result.hasErrors()) {
            model.addAttribute("registrationDto", registrationDto);
            return "/registration";
        }
        employeeService.saveEmployee(registrationDto);
        return "redirect:/registration?success";
    }







}
