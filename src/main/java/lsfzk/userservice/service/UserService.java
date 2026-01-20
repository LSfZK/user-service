package lsfzk.userservice.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lsfzk.events.PromoteResponseEvent;
import lsfzk.userservice.dto.*;
import lsfzk.userservice.enums.Role;
import lsfzk.events.PromoteRequestEvent;
import lsfzk.userservice.model.PromoteRequest;
import lsfzk.userservice.model.User;
import lsfzk.userservice.repository.PromoteRequestRepository;
import lsfzk.userservice.repository.UserRepository;
import lsfzk.userservice.security.JwtUtil;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final KafkaProducerService kafkaProducerService;
    private final PromoteRequestRepository promoteRequestRepository;

    //register
    public void register(SignupDTO signupDTO){

        if (userRepository.findByEmail(signupDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

//        if (userRepository.findByPhoneNumber(signupDTO.getPhoneNumber()).isPresent()) {
//            throw new IllegalArgumentException("이미 사용 중인 전화번호입니다.");
//        }

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

        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getRoles(), user.getNickname());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        return new TokenResponseDTO(accessToken, refreshToken);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }

    @Transactional
    public UserInfoResponseDTO updateUser(Long id, UserUpdateDto dto) {
        // 1. Fetch the existing entity (Managed State)
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // 2. "Query Builder" Logic (Dynamic Update)
        // We only update the field if the DTO value is NOT NULL
        if (StringUtils.hasText(dto.getName())) {
            user.setName(dto.getName());
        }

        if (StringUtils.hasText(dto.getPhoneNumber())) {
            user.setPhoneNumber(dto.getPhoneNumber());
        }

        if (StringUtils.hasText(dto.getAddress())) {
            user.setAddress(dto.getAddress());
        }

        // 3. NO userRepository.save(user) needed!
        // Because of @Transactional, Hibernate compares the 'user' object
        // at the start vs end of the method. It sees 'address' changed,
        // so it automatically generates: "UPDATE users SET address = ? WHERE id = ?"

        return new UserInfoResponseDTO(
                user.getId(),
                user.getName(),
                user.getNickname(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getAddress(),
                user.getRoles(),
                user.getGrade(),
                user.getIsDeleted(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getDeletedAt()
        );
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
                user.getRoles(),
                user.getGrade(),
                user.getIsDeleted(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getDeletedAt()
        );
    }

    @Transactional
    public void updateUserRole(Long id, Role newRole) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        // 1. Add the new role (e.g., Add ADMIN to existing USER)
        // Since it's a Set, duplicates are automatically handled.
        user.addRole(newRole);
    }

    @Transactional
    public void requestPromote(Long id, Role newRole) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        PromoteRequest result = promoteRequestRepository.save(new PromoteRequest(id, newRole));

        PromoteRequestEvent event = new PromoteRequestEvent(
                id,
                result.getId(),
                newRole.name()
        );
        kafkaProducerService.sendPromoteRequestEvent(event);
    }

    @Transactional
    public void approve(Long requestId, String status, Long adminId) {
        PromoteRequest request = promoteRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request Id: " + requestId));
        if (status.equals("approve")) {
            request.approve(adminId);
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            user.addRole(Role.ROLE_OWNER);
            kafkaProducerService.sendPromoteResponseEvent(new PromoteResponseEvent(request.getUserId(),
                    requestId,
                    adminId,
                    true));
        } else {
            request.reject(adminId);
            kafkaProducerService.sendPromoteResponseEvent(new PromoteResponseEvent(request.getUserId(),
                    requestId,
                    adminId,
                    false));
        }
    }
}
