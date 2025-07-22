package lsfzk.userservice.service;

import lombok.RequiredArgsConstructor;
import lsfzk.userservice.dto.LoginRequestDTO;
import lsfzk.userservice.dto.SignupDTO;
import lsfzk.userservice.dto.TokenResponseDTO;
import lsfzk.userservice.dto.UserInfoResponseDTO;
import lsfzk.userservice.model.User;
import lsfzk.userservice.repository.UserRepository;
import lsfzk.userservice.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    //register
    public void register(SignupDTO signupDTO){

        if (userRepository.findByEmail(signupDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        if (userRepository.findByPhoneNumber(signupDTO.getPhoneNumber()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 전화번호입니다.");
        }

        User user = new User();
        user.setEmail(signupDTO.getEmail());
        user.setPassword(passwordEncoder.encode(signupDTO.getPassword()));
        user.setName(signupDTO.getName());
        user.setNickname(signupDTO.getNickname());
        user.setEmail(signupDTO.getEmail());
        user.setPhoneNumber(signupDTO.getPhoneNumber());
        userRepository.save(user);
    }

    public TokenResponseDTO login(LoginRequestDTO dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        if(user.getIsDeleted().equals("Y")){
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getRole(), user.getNickname());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        return new TokenResponseDTO(accessToken, refreshToken);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }

    @Transactional
    public void updateUser(Long id, Map<String, Object> updates) {
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (updates.containsKey("name")) {
            user.setName((String) updates.get("name"));
        }
        if (updates.containsKey("email")) {
            user.setEmail((String) updates.get("email"));
        }
        if (updates.containsKey("nickname")) {
            user.setNickname((String) updates.get("nickname"));
        }
        if (updates.containsKey("phoneNumber")) {
            user.setPhoneNumber((String) updates.get("phoneNumber"));
        }
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        user.setIsDeleted("Y");
    }

    public UserInfoResponseDTO getUserInfo(Long id) {
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        return new UserInfoResponseDTO(
                user.getId(),
                user.getName(),
                user.getNickname(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getAddress(),
                user.getRole(),
                user.getGrade(),
                user.getIsDeleted(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getDeletedAt()
        );
    }

    @Transactional
    public void promote(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        user.setRole("USER,ADMIN");
    }
}
