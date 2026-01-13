package lsfzk.userservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lsfzk.userservice.entity.BaseTimeEntity;
import lsfzk.userservice.enums.Role;

@Entity
@NoArgsConstructor
@Getter
public class PromoteRequest extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long userId;

    @NotNull
    private Role role;

    private boolean approved = false;
    private Long adminId;

    public PromoteRequest(Long userId, Role role) {
        this.userId = userId;
        this.role = role;
    }
}
