package com.example.employeedirectoryproject.repository;

import com.example.employeedirectoryproject.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query(value = "SELECT p FROM Project AS p INNER JOIN Status AS s ON p.status.statusId = s.statusId")
    List<Project> getListProjects();
}
