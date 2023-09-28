package com.example.employeedirectoryproject.service.serviceImpl;

import com.example.employeedirectoryproject.dto.SaveProjectDTO;
import com.example.employeedirectoryproject.mapper.ProjectMapper;
import com.example.employeedirectoryproject.model.Employee;
import com.example.employeedirectoryproject.model.Project;
import com.example.employeedirectoryproject.repository.ProjectRepository;
import com.example.employeedirectoryproject.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectServiceImpl implements ProjectService {
    private ProjectRepository projectRepository;

    @Autowired
    public ProjectServiceImpl(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public void addNewProject(SaveProjectDTO saveProjectDTO) {
        Project project = ProjectMapper.PROJECT_MAPPER.mapToProject(saveProjectDTO);
        for (Employee employee: saveProjectDTO.getEmployees()) {
            employee.getProjects().add(project);
        }
        projectRepository.save(project);
    }
}
