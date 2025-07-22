package lsfzk.userservice.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lsfzk.userservice.common.dto.Result;
import lsfzk.userservice.dto.BusinessRegistrationRequestDTO;
import lsfzk.userservice.dto.BusinessRegistrationResult;
import lsfzk.userservice.model.User;
import lsfzk.userservice.service.BusinessRegistrationService;
import lsfzk.userservice.service.FileStorageService;
import lsfzk.userservice.service.KafkaProducerService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
@Tag(name = "Business Registration", description = "사업자 신청 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/business-registrations")
public class BusinessRegistrationController {

    private final BusinessRegistrationService businessRegistrationService;
    private final FileStorageService fileStorageService;
    private final KafkaProducerService kafkaProducerService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Result<?>> register(
            @ModelAttribute BusinessRegistrationRequestDTO requestDto,
            Principal principal
            ) {
        if (!"application/pdf".equals(requestDto.getBusinessLicense().getContentType())) {
            return ResponseEntity.badRequest().body(Result.error("Only PDF files are allowed."));
        }

        String fileName;
        String fileUri;
        try {
            // Save the file and get its unique filename
            fileName = fileStorageService.storeFile(requestDto.getBusinessLicense());

            // Create the URL that can be used to download the file
            fileUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/files/download/")
                    .path(fileName)
                    .toUriString();
        } catch (IOException ex) {
            return ResponseEntity.internalServerError().body(Result.error("Could not store file. Please try again!"));
        }
        Long userId = Long.parseLong(principal.getName());
        BusinessRegistrationResult result = businessRegistrationService.register(userId, requestDto, fileName, fileUri);
        return ResponseEntity.ok(Result.success(result));
    }

    @GetMapping
    public ResponseEntity<List<BusinessRegistrationResult>> getMyRegistrations(
            @AuthenticationPrincipal User user
    ) {
        Long userId = user.getId();
        List<BusinessRegistrationResult> list = businessRegistrationService.getMyRegistrations(userId);
        return ResponseEntity.ok(list);
    }
}
