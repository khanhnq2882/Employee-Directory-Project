package com.example.employeedirectoryproject.controller;

import com.example.employeedirectoryproject.dto.SaveDepartmentDTO;
import com.example.employeedirectoryproject.model.Department;
import com.example.employeedirectoryproject.model.Employee;
import com.example.employeedirectoryproject.service.DepartmentService;
import com.example.employeedirectoryproject.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class DepartmentController {

    private DepartmentService departmentService;
    private EmployeeService employeeService;

    @Autowired
    public DepartmentController(DepartmentService departmentService, EmployeeService employeeService) {
        this.departmentService = departmentService;
        this.employeeService = employeeService;
    }

    @GetMapping("/add_new_department")
    public String addDepartmentForm(Model model) {
        model.addAttribute("currentEmployee", employeeService.getCurrentEmployee());
        model.addAttribute("saveDepartmentDTO", new SaveDepartmentDTO());
        return "add_new_department";
    }

    @PostMapping("/add_new_department")
    public String addDepartment(@Valid @ModelAttribute("saveDepartmentDTO") SaveDepartmentDTO saveDepartmentDTO) {
        departmentService.addDepartment(saveDepartmentDTO);
        return "redirect:/list_departments";
    }

    @GetMapping("/list_departments")
    public String getAllDepartments(Model model) {
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("currentEmployee", employeeService.getCurrentEmployee());
        return "list_departments";
    }

    @GetMapping("/update_department/{id}")
    public String updateDepartmentForm(@PathVariable("id") Long id, Model model) {
        Department department = departmentService.getDepartmentById(id);
        model.addAttribute("currentEmployee", employeeService.getCurrentEmployee());
        model.addAttribute("department", department);
        return "update_department";
    }

    @PostMapping("/update_department/{id}")
    public String updateDepartment(@PathVariable("id") Long id,@Valid @ModelAttribute("saveDepartmentDTO") SaveDepartmentDTO saveDepartmentDTO) {
        departmentService.updateDepartment(saveDepartmentDTO, id);
        return "redirect:/list_departments";
    }

    @GetMapping("/department_employees/{id}")
    public String getEmployeesByDepartment(@PathVariable("id") Long id, Model model) {
        List<Employee> employees = departmentService.getEmployeesByDepartment(id);
        model.addAttribute("currentEmployee", employeeService.getCurrentEmployee());
        model.addAttribute("employees", employees);
        return "department_employees";
    }

    @GetMapping("/delete_department/{id}")
    public String deleteDepartment(@PathVariable("id") Long id) {
        departmentService.deleteDepartment(id);
        return "redirect:/list_departments";
    }

}
