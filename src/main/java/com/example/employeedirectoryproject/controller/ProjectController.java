package com.example.employeedirectoryproject.controller;

import com.example.employeedirectoryproject.dto.SaveProjectDTO;
import com.example.employeedirectoryproject.mapper.ProjectMapper;
import com.example.employeedirectoryproject.model.Project;
import com.example.employeedirectoryproject.repository.EmployeeRepository;
import com.example.employeedirectoryproject.repository.StatusRepository;
import com.example.employeedirectoryproject.service.EmployeeService;
import com.example.employeedirectoryproject.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ProjectController {
    private EmployeeRepository employeeRepository;
    private StatusRepository statusRepository;
    private ProjectService projectService;

    @Autowired
    public ProjectController(EmployeeRepository employeeRepository,
                             ProjectService projectService,
                             StatusRepository statusRepository) {
        this.employeeRepository = employeeRepository;
        this.projectService = projectService;
        this.statusRepository = statusRepository;
    }

    @GetMapping("/add_new_project")
    public String addProjectForm(Model model) {
        model.addAttribute("employees", employeeRepository.getActiveEmployees());
        model.addAttribute("statusList", statusRepository.findAll());
        model.addAttribute("saveProjectDto", new SaveProjectDTO());
        return "add_new_project";
    }

    @PostMapping("/add_new_project")
    public String addNewProject(@Valid @ModelAttribute("saveProjectDto")SaveProjectDTO saveProjectDTO) {
        projectService.addNewProject(saveProjectDTO);
        return "redirect:/list_employees";
    }

    @GetMapping("/list_projects")
    public String getListProjects(Model model) {
        model.addAttribute("projects", projectService.getListProjects());
        return "list_projects";
    }

    @GetMapping("/update_project/{id}")
    public String getUpdateProjectForm(@PathVariable("id") Long id, Model model) {
        Project project = projectService.getProjectById(id);
        SaveProjectDTO saveProjectDTO = ProjectMapper.PROJECT_MAPPER.mapToSaveProjectDTO(project);
        model.addAttribute("project", project);
        model.addAttribute("saveProjectDTO", saveProjectDTO);
        model.addAttribute("employees", employeeRepository.getActiveEmployees());
        model.addAttribute("statusList", statusRepository.findAll());
        return "update_project";
    }

    @PostMapping("/update_project/{id}")
    public String updateProject(@Valid @ModelAttribute("saveProjectDTO") SaveProjectDTO saveProjectDTO,
                                @PathVariable("id") Long id,
                                Model model) {
        projectService.updateProject(saveProjectDTO, id);
        return "redirect:/list_projects";
    }

}
