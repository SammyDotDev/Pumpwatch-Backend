package com.devnaza.pumpwatch.modules.user.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@Builder
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(name = "uk_users_email", columnNames = "email"),
        @UniqueConstraint(name = "uk_users_phone_number", columnNames =
                                                                  "phone_number"),
        @UniqueConstraint(name = "uk_users_username", columnNames = "username")
})
public class User {

    private final static int MAX_LENGTH = 20;

    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true, nullable = false, updatable = false, length = MAX_LENGTH)
    private String userId;

    @Column
    private String firstName;

    @Column
    private String lastName;
    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String phoneNumber;

    private String password;

    @PrePersist
    public void generateUserId(){
        this.userId = java.util.UUID.randomUUID().toString().substring(0,MAX_LENGTH).replace("-","");
    }


}
