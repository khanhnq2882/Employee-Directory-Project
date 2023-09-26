package com.example.employeedirectoryproject.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "employee")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employeeId;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Boolean gender;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date startWork;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endWork;

    @Column(nullable = false)
    private Double coefficientsSalary;

    @Column(nullable = false)
    private Boolean status;

//    @Column(nullable = false)
//@CreationTimestamp
//    private String createdBy;
//
//    @Column(nullable = false)
//    private Date createdDay;
//
//    @Column(nullable = false)
//    private String updatedBy;
//
//    @Column(nullable = false)
//    @UpdateTimestamp
//    private Date updatedDay;

    @ManyToOne
    @JoinColumn(name = "department_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Department department;

    @ManyToOne
    @JoinColumn(name = "position_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Position position;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JoinTable(
            name = "employees_skills",
            joinColumns = {@JoinColumn(name = "employee_id")},
            inverseJoinColumns = {@JoinColumn(name = "skill_id")})
    private Set<Skill> skills = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JoinTable(name = "project_member",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "project_id"))
    private Set<Project> projects = new HashSet<>();

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<Certification> certifications = new ArrayList<>();

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<Experience> experiences = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "employees_roles",
            joinColumns = {@JoinColumn(name = "employee_id", referencedColumnName = "employeeId")},
            inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "roleId")})
    private List<Role> roles = new ArrayList<>();

    public Employee(String firstName, String lastName, String email, String password,
                    Boolean gender, Date dateOfBirth, String phoneNumber, String address,
                    Date startWork, Date endWork, Double coefficientsSalary, Boolean status,
                    Department department, Position position, List<Role> roles) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.startWork = startWork;
        this.endWork = endWork;
        this.coefficientsSalary = coefficientsSalary;
        this.status = status;
        this.department = department;
        this.position = position;
        this.roles = roles;
    }

    public Employee(String firstName, String lastName, String email, String password,
                    Boolean gender, Date dateOfBirth, String phoneNumber, String address,
                    Date startWork, Date endWork, Double coefficientsSalary, Boolean status,
                    Department department, Position position) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.startWork = startWork;
        this.endWork = endWork;
        this.coefficientsSalary = coefficientsSalary;
        this.status = status;
        this.department = department;
        this.position = position;
    }
}
