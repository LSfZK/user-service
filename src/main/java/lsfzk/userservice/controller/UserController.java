package lsfzk.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lsfzk.userservice.dto.LoginRequestDTO;
import lsfzk.userservice.dto.SignupDTO;
import lsfzk.userservice.dto.TokenResponseDTO;
import lsfzk.userservice.model.User;
import lsfzk.userservice.security.JwtUtil;
import lsfzk.userservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> register(@RequestBody SignupDTO signupDTO){
        userService.register(signupDTO);
        return ResponseEntity.ok("회원가입 성공!");
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
    public ResponseEntity<?> refresh(@RequestHeader("Authorization") String refreshToken) {
        if (refreshToken.startsWith("Bearer ")) {
            refreshToken = refreshToken.substring(7);
        }

        if (!jwtUtil.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }

        List<String> claims = jwtUtil.getClaimsFromToken(refreshToken);
        if (claims.size() < 3) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token claims");
        }
        Long userId = Long.parseLong(claims.get(0));
        String roles = claims.get(1);
        String nickname = claims.get(2);
        String newAccessToken = jwtUtil.generateAccessToken(userId, roles, nickname);

        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "현재 접속중인 사용자 로그아웃")
    public ResponseEntity<?> logout() {

        return ResponseEntity.ok("로그아웃 되었습니다.");
    }

    @PatchMapping("/{id}")
    @Operation(summary = "회원정보 수정", description = "회원정보 부분 및 전체 수정")
    public ResponseEntity<?> updateUser(@PathVariable Long id,
                                        @RequestBody Map<String, Object> updates) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (!id.equals(Long.parseLong(authentication.getName()))) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("접근 권한이 없습니다.");
//        }

        userService.updateUser(id, updates);
        return ResponseEntity.ok("회원정보가 수정되었습니다.");
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

}
