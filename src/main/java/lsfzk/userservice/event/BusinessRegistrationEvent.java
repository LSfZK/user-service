package lsfzk.userservice.event;

public record BusinessRegistrationEvent(
        Long userId,
        Long registrationId,
        String userNickname,
        String businessName
) {}
