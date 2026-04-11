package com.devnaza.pumpwatch.modules.user.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(name = "uk_users_email", columnNames = "email"),
        @UniqueConstraint(name = "uk_users_phone_number", columnNames =
                                                                  "phone_number"),
        @UniqueConstraint(name = "uk_users_username", columnNames = "username")
})
public class User {

    @Setter
    @Getter
    @Id
    @GeneratedValue
    private UUID id;

    @Getter
    @Setter
    @Column
    private String firstName;

    @Getter
    @Setter
    @Column
    private String lastName;

    @Getter
    @Setter
    @Column(unique = true, nullable = false)
    private String username;

    @Getter
    @Setter
    @Column(unique = true, nullable = false)
    private String email;

    @Getter
    @Setter
    @Column(nullable = false)
    private String phoneNumber;

    @Getter
    @Setter
    private String password;

    public User() {

    }

    public User(UUID id, String username, String email, String phoneNumber, String password) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }


}
