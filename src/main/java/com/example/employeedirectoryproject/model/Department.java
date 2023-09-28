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
@Builder
@Table(name = "department")
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long departmentId;

    @Column(nullable = false)
    private String departmentName;

    @Column(nullable = false)
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

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<Employee> employees;
}
