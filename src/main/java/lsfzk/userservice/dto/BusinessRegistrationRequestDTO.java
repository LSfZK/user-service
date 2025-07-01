package lsfzk.userservice.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BusinessRegistrationRequestDTO {
    private String businessName;
    private String businessNumber;
    private String address;
}
