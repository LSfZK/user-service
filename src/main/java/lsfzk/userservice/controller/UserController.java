package lsfzk.userservice.controller;

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

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody SignupDTO signupDTO){
        userService.register(signupDTO);
        return ResponseEntity.ok("회원가입 성공!");
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody LoginRequestDTO dto) {
        TokenResponseDTO token = userService.login(dto);
        return ResponseEntity.ok(token);
    }

    @GetMapping("/{id}")
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
                "loginId", user.getLoginId(),
                "name", user.getName(),
                "email", user.getEmail(),
                "nickname", user.getNickname(),
                "phoneNumber", user.getPhoneNumber()
        ));
    }

    @PostMapping("/refresh")
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
    public ResponseEntity<?> logout() {

        return ResponseEntity.ok("로그아웃 되었습니다.");//fake redis 활용한 블랙리스트로 구현할것
    }

    @GetMapping("/debug")
    public ResponseEntity<?> debug(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        return ResponseEntity.ok(Map.of(
                "principal_class", principal.getClass().getName(),
                "principal_value", principal.toString()
        ));
    }
}
