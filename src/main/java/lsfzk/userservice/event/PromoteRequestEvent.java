package lsfzk.userservice.event;

import lsfzk.userservice.enums.Role;

public record PromoteRequestEvent (
    Long userId,
    Long requestId,
    Role newRole
) {}
