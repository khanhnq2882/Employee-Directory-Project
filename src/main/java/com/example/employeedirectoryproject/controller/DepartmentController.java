package com.example.employeedirectoryproject.controller;

import com.example.employeedirectoryproject.model.Department;
import com.example.employeedirectoryproject.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @GetMapping("/showAddDepartmentForm")
    public String showAddDepartmentForm(Model model) {
        Department department = new Department();
        model.addAttribute("department", department);
        return "add_department";
    }

    @PostMapping("/addDepartment")
    public String addDepartment(@ModelAttribute("department") Department department) {
        departmentService.addDepartment(department);
        return "redirect:departments";
    }

    @GetMapping("/departments")
    public String getAllDepartments(Model model) {
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "department";
    }

    @GetMapping("/deleteDepartment/{id}")
    public String deleteDepartment(@PathVariable(value = "id") Long id) {
        departmentService.deleteDepartment(id);
        return "redirect:departments";
    }

}
