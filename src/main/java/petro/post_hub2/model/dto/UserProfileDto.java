package petro.post_hub2.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import petro.post_hub2.model.enums.RegistrationStatus;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDto implements Serializable {
    private int id;
    private String username;
    private String email;
    private LocalDateTime updated;
    private RegistrationStatus registrationStatus;
    private LocalDateTime lastLogin;
    private String token;
    private String refreshToken;
    private Collection<RoleDto> roles;
}
