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
@Table(name = "project")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectId;

    @Column(nullable = false)
    private String projectName;

    @Column(nullable = false)
    private String language;

    @Column(nullable = false)
    private String framework;

    @Column(nullable = true)
    private String description;

    @Column(nullable = false)
    private Date startDate;

    @Column(nullable = false)
    private Date endDate;

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

    @ManyToMany(mappedBy = "projects")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<Employee> employees;

}
