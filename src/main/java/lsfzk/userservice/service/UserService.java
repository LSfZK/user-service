package lsfzk.userservice.service;

import lombok.RequiredArgsConstructor;
import lsfzk.userservice.dto.LoginRequestDTO;
import lsfzk.userservice.dto.SignupDTO;
import lsfzk.userservice.dto.TokenResponseDTO;
import lsfzk.userservice.model.User;
import lsfzk.userservice.repository.UserRepository;
import lsfzk.userservice.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    //register
    public void register(SignupDTO signupDTO){
        if(userRepository.findByLoginId(signupDTO.getLoginId()).isPresent()){
            throw new IllegalArgumentException("이미 사용 중인 ID입니다.");
        }

        if (userRepository.findByEmail(signupDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        if (userRepository.findByPhoneNumber(signupDTO.getPhoneNumber()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 전화번호입니다.");
        }

        User user = new User();
        user.setLoginId(signupDTO.getLoginId());
        user.setPassword(passwordEncoder.encode(signupDTO.getPassword()));
        user.setName(signupDTO.getName());
        user.setNickname(signupDTO.getNickname());
        user.setEmail(signupDTO.getEmail());
        user.setPhoneNumber(signupDTO.getPhoneNumber());
        userRepository.save(user);
    }

    public TokenResponseDTO login(LoginRequestDTO dto) {
        User user = userRepository.findByLoginId(dto.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getRole(), user.getLoginId());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());


        return new TokenResponseDTO(accessToken, refreshToken);
    }
}
