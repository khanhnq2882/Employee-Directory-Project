package com.example.employeedirectoryproject.controller;

import com.example.employeedirectoryproject.dto.SaveProjectDTO;
import com.example.employeedirectoryproject.repository.EmployeeRepository;
import com.example.employeedirectoryproject.service.EmployeeService;
import com.example.employeedirectoryproject.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ProjectController {
    private EmployeeRepository employeeRepository;
    private ProjectService projectService;



    @Autowired
    public ProjectController(EmployeeRepository employeeRepository,
                             ProjectService projectService) {
        this.employeeRepository = employeeRepository;
        this.projectService = projectService;
    }

    @GetMapping("add_new_project")
    public String addProjectForm(Model model) {
        model.addAttribute("employees", employeeRepository.getActiveEmployees());
        model.addAttribute("saveProjectDto", new SaveProjectDTO());
        return "add_new_project";
    }

    @PostMapping("add_new_project")
    public String addNewProject(@Valid @ModelAttribute("saveProjectDto")SaveProjectDTO saveProjectDTO) {
        projectService.addNewProject(saveProjectDTO);
        return "redirect:/list_employees";
    }

}
