package com.example.employeedirectoryproject.dto;

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
    private Date startDate;
    private Date endDate;
    private String description;
}
