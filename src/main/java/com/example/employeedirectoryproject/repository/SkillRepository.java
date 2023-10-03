package com.example.employeedirectoryproject.repository;

import com.example.employeedirectoryproject.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {
    @Query("SELECT s FROM Skill AS s INNER JOIN Employee AS e WHERE e.employeeId = :employeeId")
    List<Skill> getEmployeeSkills(Long employeeId);
}
