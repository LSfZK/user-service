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
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity<String> register(@RequestBody SignupDTO signupDTO){
        userService.register(signupDTO);
        return ResponseEntity.ok("회원가입 성공!");
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody LoginRequestDTO dto) {
        TokenResponseDTO token = userService.login(dto);
        return ResponseEntity.ok(token);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(Map.of(
                "loginId", user.getLoginId(),
                "nickname", user.getNickname()
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

        String loginId = jwtUtil.getLoginIdFromToken(refreshToken);
        String newAccessToken = jwtUtil.generateAccessToken(loginId);

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
