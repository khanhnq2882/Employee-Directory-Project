package com.example.employeedirectoryproject.service.serviceImpl;

import com.example.employeedirectoryproject.dto.CertificationDto;
import com.example.employeedirectoryproject.model.Certification;
import com.example.employeedirectoryproject.model.Employee;
import com.example.employeedirectoryproject.repository.CertificationRepository;
import com.example.employeedirectoryproject.repository.EmployeeRepository;
import com.example.employeedirectoryproject.service.CertificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CertificationServiceImpl implements CertificationService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CertificationRepository certificationRepository;

    @Override
    public void addCertification(CertificationDto certificationDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee currentEmployee = employeeRepository.findByEmail(authentication.getName());
        Certification certification = Certification.builder()
                .certificationName(certificationDto.getCertificationName())
                .issuedDate(certificationDto.getIssuedDate())
                .expiredDate(certificationDto.getExpiredDate())
                .description(certificationDto.getDescription())
                .employee(currentEmployee)
                .build();
        certificationRepository.save(certification);
    }
}
