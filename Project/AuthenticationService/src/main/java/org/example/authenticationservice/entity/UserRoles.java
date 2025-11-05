package org.example.authenticationservice.entity;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "user_roles")
public class UserRoles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String salt;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    public UserRoles(String username, String password, String salt, Set<Role> roles) {
        this.username = username;
        this.password = password;
        this.salt = salt;
        this.roles = roles;
    }

    public UserRoles() {
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
    public Set<Role> getRoles() {
        return roles;
    }

    public String getSalt() {
        return salt;
    }

    public Object getId() {
        return id;
    }
}
