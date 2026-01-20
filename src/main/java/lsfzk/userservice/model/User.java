package lsfzk.userservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lsfzk.userservice.config.converter.RoleSetConverter;
import lsfzk.userservice.enums.Role;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email"),
                @UniqueConstraint(columnNames = "phone_number")}
)
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String password;

    @Column(length = 50, nullable = false)
    private String name;

    private String nickname;

    @Column(nullable = false)
    private String email;

    private String phoneNumber;

    private String profileImage;

    private String profileDirectory;

    private String address;

    @Column(name = "roles", nullable = false)
    @Convert(converter = RoleSetConverter.class) // <--- Apply the magic here
    // ✅ Initialize with a Set containing ROLE_USER
    private Set<Role> roles = new HashSet<>(Collections.singletonList(Role.ROLE_USER));

    @Column(nullable = false)
    private String grade = "BASIC";

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    @Column(length = 1, nullable = false)
    private String isDeleted = "N";

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Helper to add roles easily
    public void addRole(Role role) {
        this.roles.add(role);
    }
}
