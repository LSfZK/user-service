package lsfzk.userservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lsfzk.userservice.enums.BusinessRegistrationStatus;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Table(name = "business_registration")
public class BusinessRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String businessName;

    @Column(nullable = false)
    private String address;

    private String image;
    private String directory;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BusinessRegistrationStatus status = BusinessRegistrationStatus.PENDING;

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
}