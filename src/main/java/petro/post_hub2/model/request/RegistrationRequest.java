package petro.post_hub2.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequest implements Serializable {
    @NotBlank(message = "username must be present")
    private String username;

    @NotBlank(message = "email must be present")
    @Email(message = "email must be in valid format")
    private String email;

    @NotBlank(message = "password must be present")
    private String password;

    @NotBlank(message = "confirmPassword must be present")
    private String confirmPassword;
}
