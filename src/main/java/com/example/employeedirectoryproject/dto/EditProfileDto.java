package com.example.employeedirectoryproject.dto;

import com.example.employeedirectoryproject.model.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EditProfileDto {
    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private Boolean gender;
    private String phoneNumber;
    private String address;
    private List<Skill> skills;
    private List<Certification> certifications;
    private List<Experience> experiences;
}
