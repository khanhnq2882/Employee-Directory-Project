package com.example.employeedirectoryproject.dto;

import com.example.employeedirectoryproject.model.Employee;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SkillDTO {
    private String skillName;
    private Integer level;
    private String description;
    private List<Employee> employees = new ArrayList<>();
}
