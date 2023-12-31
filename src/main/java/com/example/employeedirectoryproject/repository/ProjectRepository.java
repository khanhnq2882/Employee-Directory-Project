package com.example.employeedirectoryproject.repository;

import com.example.employeedirectoryproject.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Set;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query(value = "SELECT p FROM Project AS p INNER JOIN Status AS s ON p.status.statusId = s.statusId")
    List<Project> getListProjects();

    @Query(value = "SELECT p FROM Project AS p INNER JOIN Status AS s ON p.status.statusId = s.statusId")
    Page<Project> getAllProjects(Pageable pageable);

    @Query(value = "SELECT p FROM Project AS p " +
            "INNER JOIN Status AS s " +
            "ON p.status.statusId = s.statusId " +
            "WHERE p.projectName LIKE %:searchText% " +
            "OR p.language LIKE %:searchText% " +
            "OR p.framework LIKE %:searchText% " +
            "OR p.status.statusName LIKE %:searchText%")
    Page<Project> searchProjects(String searchText, Pageable pageable);

    @Query(value = "SELECT p FROM Project AS p INNER JOIN p.employees AS e WHERE e.employeeId = :employeeId")
    List<Project> getProjectsByEmployee(@Param("employeeId") Long employeeId);
}
