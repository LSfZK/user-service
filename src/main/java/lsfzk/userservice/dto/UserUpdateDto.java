package lsfzk.userservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserUpdateDto {

    private String name;
    private String nickname;
    private String phoneNumber;
    private String address;

}
