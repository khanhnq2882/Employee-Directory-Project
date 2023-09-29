package com.example.employeedirectoryproject.service;

import com.example.employeedirectoryproject.dto.SaveProjectDTO;
import com.example.employeedirectoryproject.model.Project;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProjectService {
    void addNewProject(SaveProjectDTO saveProjectDTO);
    List<Project> getListProjects();
    Project getProjectById(Long id);
    void updateProject(SaveProjectDTO saveProjectDTO, Long id);
}
