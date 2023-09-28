package com.example.employeedirectoryproject.dto;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SkillDTO {
    private String skillName;
    private Integer level;
    private String description;
}
