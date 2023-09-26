package com.example.employeedirectoryproject.repository;

import com.example.employeedirectoryproject.model.Employee;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Employee findByEmail(String email);

//    @Query(value = "SELECT * FROM employee INNER JOIN employees_roles ON employee.employee_id = employees_roles.emp_id WHERE employees_roles.role_id = 1", nativeQuery = true)
//    List<Employee> getAllEmployees();

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM employees_roles WHERE employee_id = :employeeId AND role_id = :roleId", nativeQuery = true)
    void deleteEmployeeRole(@Param("employeeId") Long employeeId, @Param("roleId") Long roleId);

    @Query(value = "select * from employee as e where e.email like %:searchText% or e.first_name like %:searchText% or e.last_name like %:searchText%", nativeQuery = true)
    List<Employee> search(@Param("searchText") String searchText);

}
