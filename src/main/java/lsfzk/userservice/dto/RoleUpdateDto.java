package lsfzk.userservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lsfzk.userservice.enums.Role;

@Getter
@NoArgsConstructor
public class RoleUpdateDto {
    private Role role;
}
