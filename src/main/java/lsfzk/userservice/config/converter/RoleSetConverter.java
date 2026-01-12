package lsfzk.userservice.config.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lsfzk.userservice.enums.Role;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Converter
public class RoleSetConverter implements AttributeConverter<Set<Role>, String> {

    private static final String SPLIT_CHAR = ",";

    @Override
    public String convertToDatabaseColumn(Set<Role> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return Role.ROLE_USER.name(); // Default to USER if empty
        }
        // Turns Set(USER, ADMIN) -> "ROLE_USER,ROLE_ADMIN"
        return attribute.stream()
                .map(Enum::name)
                .collect(Collectors.joining(SPLIT_CHAR));
    }

    @Override
    public Set<Role> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return Collections.singleton(Role.ROLE_USER);
        }
        // Turns "ROLE_USER,ROLE_ADMIN" -> Set(USER, ADMIN)
        return Arrays.stream(dbData.split(SPLIT_CHAR))
                .map(Role::valueOf)
                .collect(Collectors.toSet());
    }
}
