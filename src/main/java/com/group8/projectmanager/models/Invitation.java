package com.group8.projectmanager.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    private Timestamp sentOn;

    private boolean accepted = false;

    @OneToOne(optional = false, cascade = CascadeType.ALL)
    private Project project;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    private User sender;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    private User receiver;
}
