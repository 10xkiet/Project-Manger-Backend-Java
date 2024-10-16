package com.group8.projectmanager.models;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private Timestamp createdOn;

    private Timestamp deadline;

    @ManyToOne(optional = false)
    private User creator;

    @OneToMany(
        cascade = CascadeType.PERSIST,
        orphanRemoval = true
    )
    private List<Project> subProjects;

    @ManyToOne
    private User manager;

    @ManyToOne
    private Project parentProject;
}