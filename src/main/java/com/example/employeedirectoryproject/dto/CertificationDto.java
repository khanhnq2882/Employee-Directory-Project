package com.example.employeedirectoryproject.dto;

import com.example.employeedirectoryproject.model.Employee;
import jakarta.persistence.Column;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CertificationDto {
    private String certificationName;
    private Date issuedDate;
    private Date expiredDate;
    private String description;
    private Employee employee;
}
