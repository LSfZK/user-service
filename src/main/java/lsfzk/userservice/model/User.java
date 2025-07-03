package lsfzk.userservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users",
        uniqueConstraints = {@UniqueConstraint(columnNames = "loginId")}
)
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String loginId;
    private String password;
    private String nickname;


}
