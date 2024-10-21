package com.pulse.api.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pulse.api.enums.Gender;
import com.pulse.api.enums.RelationshipStatus;
import com.pulse.api.enums.RoleName;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String middleName;

    private String lastName;

    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String email;

    private String password;

    private String phone;

    private String country;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private RelationshipStatus relationshipStatus;

    private String profilePicture;

    private String bio;

    @JsonFormat(pattern = "EEEE MMMM dd, yyyy HH:mm")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "EEEE MMMM dd, yyyy HH:mm")
    private LocalDateTime updatedAt;


    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<RoleName> roles = new HashSet<>(); // Use RoleName enum

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
