package lsfzk.userservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class WaitingPromoteRequestDto {

    private Long requestId;
    private Long requesterId;
    private String username;
    private LocalDateTime createdAt;

    public WaitingPromoteRequestDto(Long requestId, Long requesterId, String username, LocalDateTime createdAt) {
        this.requestId = requestId;
        this.requesterId = requesterId;
        this.username = username;
        this.createdAt = createdAt;
    }
}
