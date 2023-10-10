package com.example.employeedirectoryproject.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

    @Column(nullable = true)
    private String createdBy;

    @CreationTimestamp
    private Date createdDay;

    @Column(nullable = true)
    private String updatedBy;

    @UpdateTimestamp
    private Date updatedDay;

    @OneToMany(mappedBy = "position", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<Employee> employees;

}
