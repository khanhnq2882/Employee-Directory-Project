package com.example.employeedirectoryproject.controller;

import com.example.employeedirectoryproject.dto.SaveDepartmentDTO;
import com.example.employeedirectoryproject.model.Department;
import com.example.employeedirectoryproject.model.Employee;
import com.example.employeedirectoryproject.model.Project;
import com.example.employeedirectoryproject.service.DepartmentService;
import com.example.employeedirectoryproject.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/list_departments/page/{pageNo}")
    public String getListDepartments(HttpServletRequest request,
                                  @PathVariable(value = "pageNo") int pageNo,
                                  @RequestParam("sortField") String sortField,
                                  @RequestParam("sortDirection") String sortDirection,
                                  Model model) {
        String searchText = request.getParameter("searchText");
        Page<Department> page = departmentService.findPaginated(pageNo, 3, sortField, sortDirection, searchText);
        model.addAttribute("departments", page.getContent());
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("reverseSortDirection", sortDirection.equals("asc") ? "desc" : "asc");
        model.addAttribute("currentEmployee", employeeService.getCurrentEmployee());
        return "list_departments";
    }

    @GetMapping("/list_departments")
    public String defaultListProjects(HttpServletRequest request, Model model) {
        return getListDepartments(request,1, "departmentName", "asc", model);
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
