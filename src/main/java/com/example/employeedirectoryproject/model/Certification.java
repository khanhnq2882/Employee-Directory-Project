package com.example.employeedirectoryproject.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "certification")
public class Certification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String certificationName;

    @Column(nullable = false)
    private Date issuedDate;

    @Column(nullable = false)
    private Date expiredDate;

    @Column(nullable = true)
    private String description;

//    @Column(nullable = false)
//    private String createdBy;
//
//    @Column(nullable = false)
//    private Date createdDay;
//
//    @Column(nullable = false)
//    private String updatedBy;
//
//    @Column(nullable = false)
//    private Date updatedDay;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Employee employee;

}
