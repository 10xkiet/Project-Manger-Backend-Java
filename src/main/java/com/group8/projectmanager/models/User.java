package com.group8.projectmanager.models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;
    @JoinColumn(name = "root_project_id",referencedColumnName = "id")
    @OneToOne(cascade = CascadeType.ALL)
    private Project rootProject;
    @Column(nullable = false)
    private String password;
    @Column(name="created-rojects")
    @OneToMany(mappedBy ="creator" ,cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Project> userProjects= new ArrayList<>();
    @Column(name="managed-projects")
    @OneToMany(mappedBy = "manager",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Project> managedProjects = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }
}