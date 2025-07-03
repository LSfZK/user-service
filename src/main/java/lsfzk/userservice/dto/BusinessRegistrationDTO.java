package lsfzk.userservice.dto;

import lombok.Builder;
import lombok.Data;
import lsfzk.userservice.enums.BusinessRegistrationStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class BusinessRegistrationDTO {

    private Long id;
    private String businessName;
    private String address;
    private BusinessRegistrationStatus status;
    private LocalDateTime createdAt;

    private String userName;
    private String userLoginId;
}