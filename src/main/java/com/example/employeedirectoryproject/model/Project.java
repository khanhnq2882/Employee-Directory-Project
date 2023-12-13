package com.example.employeedirectoryproject.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Column(nullable = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date endDate;

    @Column(nullable = true)
    private String createdBy;

    @CreationTimestamp
    private Date createdDay;

    @Column(nullable = true)
    private String updatedBy;

    @UpdateTimestamp
    private Date updatedDay;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "status_id", referencedColumnName = "statusId")
    private Status status;

    @ManyToMany(mappedBy = "projects")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<Employee> employees;

}
