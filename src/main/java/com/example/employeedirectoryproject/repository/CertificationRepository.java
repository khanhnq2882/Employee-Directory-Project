package com.example.employeedirectoryproject.repository;

import com.example.employeedirectoryproject.model.Certification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CertificationRepository extends JpaRepository<Certification, Long> {

    @Query(value = "SELECT c FROM Certification AS c INNER JOIN FETCH c.employee AS e WHERE e.employeeId = :employeeId")
    List<Certification> getEmployeeCertifications(@Param("employeeId") Long employeeId);
}
