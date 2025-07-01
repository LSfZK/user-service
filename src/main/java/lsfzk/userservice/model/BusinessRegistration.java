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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String businessName;

    @Column(nullable = false, unique = true)
    private String businessNumber;//File 고려

    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BusinessRegistrationStatus status = BusinessRegistrationStatus.PENDING;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

}