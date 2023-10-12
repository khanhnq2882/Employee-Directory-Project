package com.example.employeedirectoryproject.repository;

import com.example.employeedirectoryproject.model.Employee;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Employee findByEmail(String email);

    Employee findEmployeeByPhoneNumber(String phoneNumber);

    @Query(value = "SELECT e FROM Employee AS e INNER JOIN e.roles AS r WHERE r.roleId = 1")
    List<Employee> getAllEmployees();

    @Query(value = "SELECT e FROM Employee AS e INNER JOIN e.roles AS r WHERE r.roleId = 1 AND e.status = true")
    List<Employee> getActiveEmployees();

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM employees_roles WHERE employee_id = :employeeId AND role_id = :roleId", nativeQuery = true)
    void deleteEmployeeRole(@Param("employeeId") Long employeeId, @Param("roleId") Long roleId);

    @Query(value = "SELECT * FROM employee AS e WHERE e.email LIKE %:searchText% OR e.first_name LIKE %:searchText% OR e.last_name LIKE %:searchText%", nativeQuery = true)
    List<Employee> search(@Param("searchText") String searchText);

}
