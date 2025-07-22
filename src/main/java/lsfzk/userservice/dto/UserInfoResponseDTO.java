package lsfzk.userservice.dto;

import java.time.LocalDateTime;

public record UserInfoResponseDTO(
        Long id,
        String name,
        String nickname,
        String email,
        String phoneNumber,
        String address,
        String role,
        String grade,
        String isDeleted,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {}
