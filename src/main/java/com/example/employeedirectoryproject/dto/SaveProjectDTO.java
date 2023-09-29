package com.example.employeedirectoryproject.dto;

import com.example.employeedirectoryproject.model.Employee;
import com.example.employeedirectoryproject.model.Status;
import lombok.*;
import java.util.Date;
import java.util.Set;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaveProjectDTO {
    private String projectName;
    private String language;
    private String framework;
    private Date startDate;
    private Date endDate;
    private String description;
    private Status status;
    private Set<Employee> employees;
}
