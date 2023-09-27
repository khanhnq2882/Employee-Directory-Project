package com.example.employeedirectoryproject.service.serviceImpl;

import com.example.employeedirectoryproject.dto.SaveProjectDTO;
import com.example.employeedirectoryproject.model.Employee;
import com.example.employeedirectoryproject.model.Project;
import com.example.employeedirectoryproject.repository.ProjectRepository;
import com.example.employeedirectoryproject.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Override
    public void addNewProject(SaveProjectDTO saveProjectDTO) {
        Project project = new Project();
        project.setProjectName(saveProjectDTO.getProjectName());
        project.setLanguage(saveProjectDTO.getLanguage());
        project.setFramework(saveProjectDTO.getFramework());
        project.setStartDate(saveProjectDTO.getStartDate());
        project.setEndDate(saveProjectDTO.getEndDate());
        project.setDescription(saveProjectDTO.getDescription());
        project.setEmployees(saveProjectDTO.getEmployees());
        for (Employee employee: saveProjectDTO.getEmployees()) {
            employee.getProjects().add(project);
        }
        projectRepository.save(project);
    }
}
