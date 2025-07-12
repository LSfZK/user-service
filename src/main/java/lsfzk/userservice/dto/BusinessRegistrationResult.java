package lsfzk.userservice.dto;

import lombok.Builder;
import lombok.Data;
import lsfzk.userservice.enums.BusinessRegistrationStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class BusinessRegistrationResult {

    private Long id;
    private String businessName;
    private String address;
    private BusinessRegistrationStatus status;
    private LocalDateTime createdAt;
    private String fileName;
    private String fileUri;

    private String userId;
    private String userName;
}