package com.example.employeedirectoryproject.repository;

import com.example.employeedirectoryproject.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Employee findByEmail(String email);

    @Query(value = "SELECT * FROM employee AS e INNER JOIN employees_roles AS er ON e.id = er.employee_id WHERE er.role_id = 1", nativeQuery = true)
    List<Employee> getAllEmployees();

}
