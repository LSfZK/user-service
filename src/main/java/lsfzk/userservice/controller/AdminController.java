package lsfzk.userservice.controller;

import lombok.RequiredArgsConstructor;
import lsfzk.userservice.common.dto.Result;
import lsfzk.userservice.model.BusinessRegistration;
import lsfzk.userservice.service.BusinessRegistrationService;
import lsfzk.userservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/admin")
public class AdminController {

    private final UserService userService;
    private final BusinessRegistrationService businessRegistrationService;

    @GetMapping("/business-registrations/approve/{id}")
    public ResponseEntity<Result<?>> checkBusinessRegistration(@PathVariable Long id, Authentication authentication) {
        if(authentication.getAuthorities().stream().noneMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(403).body(Result.error("Access denied. Only admins can approve registrations."));
        }
        // Logic to approve business registration
        // This method should call the userService to perform the approval
        // and return an appropriate response.
        BusinessRegistration businessRegistration = businessRegistrationService.getBusinessRegistrationById(id);
        return ResponseEntity.ok(Result.success(businessRegistration));
    }

    @PostMapping("/business-registrations/approve")
    public ResponseEntity<Result<?>> approveBusinessRegistration(Long registrationId, Authentication authentication) {
        // Logic to approve business registration
        // This method should call the userService to perform the approval
        // and return an appropriate response.
        if (registrationId == null) {
            return ResponseEntity.badRequest().body(Result.error("Registration ID is required."));
        }
        if(authentication.getAuthorities().stream().noneMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(403).body(Result.error("Access denied. Only admins can approve registrations."));
        }
        BusinessRegistration businessRegistration = businessRegistrationService.approveBusinessRegistration(registrationId);
        return ResponseEntity.ok(Result.success("Business registration approved successfully."));
    }
}
