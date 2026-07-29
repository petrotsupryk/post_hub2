package petro.post_hub2.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserSystemRole {
    USER("USER"),
    ADMIN("ADMIN"),
    SUPER_ADMIN("SUPER_ADMIN");
    private final String role;

    public static UserSystemRole fromName(String name) {
        return UserSystemRole.valueOf(name.toUpperCase());
    }
}
