package lsfzk.userservice.controller;

import lombok.RequiredArgsConstructor;
import lsfzk.userservice.dto.BusinessRegistrationDTO;
import lsfzk.userservice.dto.BusinessRegistrationRequestDTO;
import lsfzk.userservice.model.User;
import lsfzk.userservice.service.BusinessRegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/business-registrations")
public class BusinessRegistrationController {

    private final BusinessRegistrationService businessRegistrationService;

    @PostMapping
    public ResponseEntity<BusinessRegistrationDTO> register(
            @RequestBody BusinessRegistrationRequestDTO dto,
            @AuthenticationPrincipal User user
            ) {
        Long userId = user.getId();
        BusinessRegistrationDTO result = businessRegistrationService.register(userId, dto);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<List<BusinessRegistrationDTO>> getMyRegistrations(
            @AuthenticationPrincipal User user
    ) {
        Long userId = user.getId();
        List<BusinessRegistrationDTO> list = businessRegistrationService.getMyRegistrations(userId);
        return ResponseEntity.ok(list);
    }
}
