package lsfzk.userservice.dto;

import lsfzk.userservice.enums.Role;

import java.time.LocalDateTime;
import java.util.Set;

public record UserInfoResponseDTO(
        Long id,
        String name,
        String nickname,
        String email,
        String phoneNumber,
        String address,
        Set<Role> role,
        String grade,
        String isDeleted,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {}
