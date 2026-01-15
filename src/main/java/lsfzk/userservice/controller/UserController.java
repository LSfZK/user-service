package lsfzk.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lsfzk.userservice.common.dto.Result;
import lsfzk.userservice.config.converter.RoleSetConverter;
import lsfzk.userservice.dto.*;
import lsfzk.userservice.model.User;
import lsfzk.userservice.security.JwtUtil;
import lsfzk.userservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "UserService", description = "회원 관련 API")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    @Operation(summary = "회원가입", description = "사용자 회원가입 api")
    public ResponseEntity<Result<?>> register(@RequestBody SignupDTO signupDTO){
        userService.register(signupDTO);
        return ResponseEntity.ok(Result.success("register success"));
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "로그인 api")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody LoginRequestDTO dto) {
        TokenResponseDTO token = userService.login(dto);
        return ResponseEntity.ok(token);
    }

    @GetMapping("/{id}")
    @Operation(summary = "회원정보 조회", description = "본인의 회원정보 조회")
    public ResponseEntity<?> getCurrentUser(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!id.equals(Long.parseLong(authentication.getName()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("접근 권한이 없습니다.");
        }

        return ResponseEntity.ok(Map.of(
                "name", user.getName(),
                "email", user.getEmail(),
                "nickname", user.getNickname(),
                "phoneNumber", user.getPhoneNumber()
        ));
    }

    @PostMapping("/refresh")
    @Operation(summary = "accessToken refresh", description = "accessToken 갱신")
    public ResponseEntity<?> refresh(
//            @RequestHeader("Authorization") String refreshToken
            @RequestBody Map<String, String> body
    ) {
        String refreshToken = body.get("refreshToken");
        if (refreshToken.startsWith("Bearer ")) {
            refreshToken = refreshToken.substring(7);
        }

        if (!jwtUtil.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }

        Long userId = Long.parseLong(jwtUtil.getIdFromToken(refreshToken));

        // 4. VETERAN MOVE: Fetch latest data from DB
        // This ensures if user was banned or roles changed, the new token reflects it.
        User user = userService.getUserById(userId);

        // 5. Generate new Access Token with FRESH data
        String newAccessToken = jwtUtil.generateAccessToken(
                user.getId(),
                user.getRoles(), // Assuming Role is an Enum
                user.getNickname()
        );

        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "현재 접속중인 사용자 로그아웃")
    public ResponseEntity<Result<?>> logout() {
        return ResponseEntity.ok(Result.success("logout"));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "회원정보 수정", description = "회원정보 부분 및 전체 수정")
    public ResponseEntity<Result<UserInfoResponseDTO>> updateUser(
            @PathVariable Long targetId,
            @RequestBody UserUpdateDto updateDto,
            @RequestHeader("X-User-Id") Long requesterId) { // From Gateway

        // 1. Security Check: Are you trying to update someone else?
        // (Unless you are an Admin, which logic you can add later)
        if (!targetId.equals(requesterId)) {
            throw new AccessDeniedException("You can only update your own profile.");
        }

        UserInfoResponseDTO updatedUser = userService.updateUser(targetId, updateDto);
        return ResponseEntity.ok(Result.success(updatedUser));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "회원탈퇴", description = "회원탈퇴, 소프트 딜리트로 1년간 데이터 유지")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

//        if (!id.equals(Long.parseLong(authentication.getName()))) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("본인만 탈퇴할 수 있습니다.");
//        }

        userService.deleteUser(id);
        return ResponseEntity.ok("회원정보가 삭제되었습니다.");
    }

    @PostMapping("/me/promotion-requests")
    public ResponseEntity<Result<Long>> promoteUser(
            @RequestBody RoleUpdateDto dto,
            @RequestHeader("X-User-Id") Long requesterId,
            @RequestHeader("X-User-Roles") String requesterRoles) {

        if (requesterRoles.contains("ROLE_OWNER")) {
            throw new AccessDeniedException("Already owner.");
        }

        userService.requestPromote(requesterId, dto.getRole());
        return ResponseEntity.ok(Result.success(requesterId));
    }
}
