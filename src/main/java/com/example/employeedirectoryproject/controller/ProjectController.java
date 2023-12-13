package com.example.employeedirectoryproject.controller;

import com.example.employeedirectoryproject.dto.SaveProjectDTO;
import com.example.employeedirectoryproject.mapper.ProjectMapper;
import com.example.employeedirectoryproject.model.Employee;
import com.example.employeedirectoryproject.model.Project;
import com.example.employeedirectoryproject.repository.EmployeeRepository;
import com.example.employeedirectoryproject.repository.StatusRepository;
import com.example.employeedirectoryproject.service.EmployeeService;
import com.example.employeedirectoryproject.service.ProjectService;
import com.example.employeedirectoryproject.service.serviceImpl.ProjectServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@AllArgsConstructor
public class ProjectController {

    private EmployeeRepository employeeRepository;
    private StatusRepository statusRepository;
    private ProjectService projectService;
    private EmployeeService employeeService;

    @GetMapping("/add_new_project")
    public String addProjectForm(Model model) {
        model.addAttribute("employees", employeeRepository.getActiveEmployees());
        model.addAttribute("statusList", statusRepository.findAll());
        model.addAttribute("saveProjectDto", new SaveProjectDTO());
        model.addAttribute("currentEmployee", employeeService.getCurrentEmployee());
        return "add_new_project";
    }

    @PostMapping("/add_new_project")
    public String addNewProject(@Valid @ModelAttribute("saveProjectDto")SaveProjectDTO saveProjectDTO) {
        projectService.addNewProject(saveProjectDTO);
        return "redirect:/list_projects";
    }

    @GetMapping("/update_project/{id}")
    public String getUpdateProjectForm(@PathVariable("id") Long id, Model model) {
        Project project = projectService.getProjectById(id);
        SaveProjectDTO saveProjectDTO = ProjectMapper.PROJECT_MAPPER.mapToSaveProjectDTO(project);
        model.addAttribute("project", project);
        model.addAttribute("saveProjectDTO", saveProjectDTO);
        model.addAttribute("employees", employeeRepository.getActiveEmployees());
        model.addAttribute("statusList", statusRepository.findAll());
        model.addAttribute("currentEmployee", employeeService.getCurrentEmployee());
        return "update_project";
    }

    @PostMapping("/update_project/{id}")
    public String updateProject(@Valid @ModelAttribute("saveProjectDTO") SaveProjectDTO saveProjectDTO,
                                @PathVariable("id") Long id) {
        projectService.updateProject(saveProjectDTO, id);
        return "redirect:/list_projects";
    }

    @GetMapping("/delete_project/{id}")
    public String deleteProject(@PathVariable("id") Long id) {
        projectService.deleteProject(id);
        return "redirect:/list_projects";
    }

    @GetMapping("/list_projects/page/{pageNo}")
    public String getListProjects(HttpServletRequest request,
                                   @PathVariable(value = "pageNo") int pageNo,
                                   @RequestParam("sortField") String sortField,
                                   @RequestParam("sortDirection") String sortDirection,
                                   Model model) {
        String searchText = request.getParameter("searchText");
        Page<Project> page = projectService.findPaginated(pageNo, 3, sortField, sortDirection, searchText);
        model.addAttribute("projects", page.getContent());
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("reverseSortDirection", sortDirection.equals("asc") ? "desc" : "asc");
        model.addAttribute("currentEmployee", employeeService.getCurrentEmployee());
        return "list_projects";
    }


    @GetMapping("/list_projects")
    public String defaultListProjects(HttpServletRequest request, Model model) {
        return getListProjects(request,1, "projectName", "asc", model);
    }

    @GetMapping("/project_export_excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=list_projects_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);
        ProjectServiceImpl excelExporter;
        if (employeeService.getCurrentEmployee().getRoles().stream().filter(role -> role.getName().equals("ROLE_ADMIN")).findAny().isPresent()) {
            excelExporter = new ProjectServiceImpl(projectService.getListProjects());
        } else {
            excelExporter = new ProjectServiceImpl(projectService.getProjectsByEmployee(employeeService.getCurrentEmployee().getEmployeeId()));
        }
        excelExporter.export(response);
    }

    @GetMapping("/employee_projects/{id}")
    public String getProjectsByEmployee(@PathVariable("id") Long id, Model model) {
        Employee currentEmployee = employeeService.getCurrentEmployee();
        List<Project> projects = projectService.getProjectsByEmployee(currentEmployee.getEmployeeId());
        model.addAttribute("currentEmployee", currentEmployee);
        model.addAttribute("projects", projects);
        return "employee_projects";
    }

}
