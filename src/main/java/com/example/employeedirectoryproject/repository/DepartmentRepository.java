package com.example.employeedirectoryproject.repository;

import com.example.employeedirectoryproject.model.Department;
import com.example.employeedirectoryproject.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    @Query(value = "SELECT e FROM Employee AS e WHERE e.department.departmentId = :departmentId")
    List<Employee> getEmployeesByDepartment(@Param("departmentId") Long departmentId);
}
