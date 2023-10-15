package com.example.employeedirectoryproject.service;

import com.example.employeedirectoryproject.dto.SaveProjectDTO;
import com.example.employeedirectoryproject.model.Employee;
import com.example.employeedirectoryproject.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProjectService {
    void addNewProject(SaveProjectDTO saveProjectDTO);
    List<Project> getListProjects();
    Project getProjectById(Long id);
    void updateProject(SaveProjectDTO saveProjectDTO, Long id);
    Page<Project> getAllProjects(Pageable pageable);
    Page<Project> searchProjects(String searchText, Pageable pageable);
    Page<Project> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection, String searchText);
}
