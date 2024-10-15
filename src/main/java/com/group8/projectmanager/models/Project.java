package com.group8.projectmanager.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Project  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String projectName;
    @Column(nullable = false)
    private LocalDateTime creationDate;
    @Column()
    private LocalDateTime endDate;
    @JoinColumn(name="creator_id",referencedColumnName = "id")
    @ManyToOne
    private User creator;
    @Column(name="sub_projects")
    @OneToMany(mappedBy = "id",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Project> subProjects = new ArrayList<>() ;
    @ManyToOne
    @JoinColumn(name = "parent_project_id",referencedColumnName = "id")
    private Project parentProject;
    @ManyToOne
    @JoinColumn(name="manager_id",referencedColumnName = "id")
    private User manager;
    @PrePersist
    protected void onCreate(){
        this.creationDate=LocalDateTime.now();
    }
}
