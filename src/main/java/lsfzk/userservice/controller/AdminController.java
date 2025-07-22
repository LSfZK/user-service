package lsfzk.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lsfzk.userservice.common.dto.Result;
import lsfzk.userservice.dto.UserInfoResponseDTO;
import lsfzk.userservice.model.BusinessRegistration;
import lsfzk.userservice.service.BusinessRegistrationService;
import lsfzk.userservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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

    @GetMapping("/user-info/{id}")
    @Operation(summary = "회원정보 조회(관리자용)", description = "userId 기반 회원정보 조회")
    public ResponseEntity<?> getUserInfo(@PathVariable Long id) {
        UserInfoResponseDTO dto = userService.getUserInfo(id);
        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "관리자 권한 부여", description = "일반 사용자에게 관리자 권한 추가")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> promote(@PathVariable Long id){
        userService.promote(id);
        return ResponseEntity.ok("관리자 권한이 부여되었습니다.");
    }
}
