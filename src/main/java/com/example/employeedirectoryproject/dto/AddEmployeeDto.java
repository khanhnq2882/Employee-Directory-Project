package com.example.employeedirectoryproject.dto;

import com.example.employeedirectoryproject.model.Department;
import com.example.employeedirectoryproject.model.Position;
import lombok.*;

import java.util.Date;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddEmployeeDto {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Date dateOfBirth;
    private String phoneNumber;
    private String address;
    private Boolean gender;
    private Date startWork;
    private Date endWork;
    private Double coefficientsSalary;
    private Department department;
    private Position position;
    private Boolean status;
}
