package lsfzk.userservice.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
//회원가입시 사용할 dto
public class SignupDTO {
    private String loginId;
    private String password;
    private String nickname;
}
