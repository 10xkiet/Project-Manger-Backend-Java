package com.group8.projectmanager.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

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

    @Enumerated(EnumType.STRING)
    private ProjectType type;

    @ColumnDefault("false")
    private Boolean isCompleted;

    @CreationTimestamp
    @Column(nullable = false)
    private Timestamp createdOn;

    private Timestamp startedOn;

    private Timestamp deadline;

    @ManyToOne(optional = false)
    private User creator;

    @ManyToOne
    private User manager;

    @ManyToOne
    private Project parentProject;

    @OneToMany(
        mappedBy = "parentProject",
        cascade = CascadeType.ALL,
        fetch = FetchType.LAZY
    )
    private List<Project> subProjects;
}