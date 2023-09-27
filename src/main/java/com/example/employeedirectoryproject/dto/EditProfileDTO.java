package com.example.employeedirectoryproject.dto;

import lombok.*;

import java.util.Date;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EditProfileDTO {
    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private Boolean gender;
    private String phoneNumber;
    private String address;
}
