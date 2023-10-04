package com.example.employeedirectoryproject.dto;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SaveDepartmentDTO {
    private String departmentName;
    private String description;
}
