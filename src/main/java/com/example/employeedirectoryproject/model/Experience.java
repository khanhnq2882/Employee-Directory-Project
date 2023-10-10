package com.example.employeedirectoryproject.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "experience")
public class Experience {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long experienceId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String language;

    @Column(nullable = false)
    private String framework;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date startWork;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date endWork;

    @Column(nullable = true)
    private String description;

    @Column(nullable = true)
    private String createdBy;

    @CreationTimestamp
    private Date createdDay;

    @Column(nullable = true)
    private String updatedBy;

    @UpdateTimestamp
    private Date updatedDay;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Employee employee;

    public Experience(String name, String companyName, String language, String framework, Date startWork, Date endWork, String description) {
        this.name = name;
        this.companyName = companyName;
        this.language = language;
        this.framework = framework;
        this.startWork = startWork;
        this.endWork = endWork;
        this.description = description;
    }
}
