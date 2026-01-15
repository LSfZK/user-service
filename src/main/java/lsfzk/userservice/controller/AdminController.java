package lsfzk.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lsfzk.userservice.common.dto.Result;
import lsfzk.userservice.dto.WaitingPromoteRequestDto;
import lsfzk.userservice.dto.RoleUpdateDto;
import lsfzk.userservice.dto.UserInfoResponseDTO;
import lsfzk.userservice.model.BusinessRegistration;
import lsfzk.userservice.model.PromoteRequest;
import lsfzk.userservice.repository.PromoteRequestRepository;
import lsfzk.userservice.service.BusinessRegistrationService;
import lsfzk.userservice.service.UserService;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/admin")
public class AdminController {

    private final UserService userService;
    private final BusinessRegistrationService businessRegistrationService;
    private final PromoteRequestRepository promoteRequestRepository;

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

//    @PatchMapping("/{id}")
//    @Operation(summary = "관리자 권한 부여", description = "일반 사용자에게 관리자 권한 추가")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<?> promote(@PathVariable Long id){
//        userService.promote(id);
//        return ResponseEntity.ok("관리자 권한이 부여되었습니다.");
//    }

    @GetMapping("/waiting-promotion-requests")
    public ResponseEntity<Result<List<WaitingPromoteRequestDto>>> getPromotionRequests(
            @RequestHeader("X-User-Roles") String roles) {

        // 1. Security: Only Admins can change roles
        if (!roles.contains("ROLE_ADMIN")) {
            throw new AccessDeniedException("Only Admins can see this page.");
        }

        List<WaitingPromoteRequestDto> results = promoteRequestRepository.findAllWaitingRequests();
        return ResponseEntity.ok(Result.success(results));
    }

    @PatchMapping("/promotion-requests/{requestId}/{status}")
    public ResponseEntity<Result<Long>> promoteUser(
            @PathVariable Long requestId,
            @PathVariable String status,
            @RequestHeader("X-User-Id") Long adminId,
            @RequestHeader("X-User-Roles") String roles) {

        // 1. Security: Only Admins can change roles
        if (!roles.contains("ROLE_ADMIN")) {
            throw new AccessDeniedException("Only Admins can promote users.");
        }

        userService.approve(requestId, status, adminId);

        return ResponseEntity.ok(Result.success(adminId));
    }
}
