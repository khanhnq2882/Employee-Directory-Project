package com.example.employeedirectoryproject.dto;

import com.example.employeedirectoryproject.model.Employee;
import lombok.*;
import java.util.Date;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExperienceDTO {
    private String companyName;
    private String projectName;
    private String language;
    private String framework;
    private Date startWork;
    private Date endWork;
    private String description;
    private Employee employee;
}
