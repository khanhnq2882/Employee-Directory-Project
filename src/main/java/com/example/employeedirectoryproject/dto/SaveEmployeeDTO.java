package com.example.employeedirectoryproject.dto;

import com.example.employeedirectoryproject.model.Department;
import com.example.employeedirectoryproject.model.Position;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.util.Date;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaveEmployeeDTO {
    @NotBlank (message = "First name must not be blank")
    private String firstName;

    private String lastName;
    private String email;
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
    private String personalEmail;
}
