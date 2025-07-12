package lsfzk.userservice.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
public class BusinessRegistrationRequestDTO {
    private String businessName;
    private String businessNumber;
    private String address;
    private MultipartFile businessLicense; // Assuming you are using Spring's MultipartFile for file uploads
    private String userNickname;
}
