package com.example.employeedirectoryproject.dto;

import lombok.*;
import java.util.Date;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CertificationDTO {
    private String certificationName;
    private Date issuedDate;
    private Date expiredDate;
    private String description;
}
