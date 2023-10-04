package com.example.employeedirectoryproject.repository;

import com.example.employeedirectoryproject.model.Certification;
import com.example.employeedirectoryproject.model.Experience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExperienceRepository extends JpaRepository<Experience, Long> {
    @Query(value = "SELECT ex FROM Experience AS ex INNER JOIN FETCH ex.employee AS e WHERE e.employeeId = :employeeId")
    List<Experience> getEmployeeExperiences(@Param("employeeId") Long employeeId);
}
