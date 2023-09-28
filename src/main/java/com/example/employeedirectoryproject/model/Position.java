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
@Table(name = "position")
public class Position {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long positionId;

    @Column(nullable = false)
    private String positionName;

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

    @OneToMany(mappedBy = "position", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<Employee> employees;

}
