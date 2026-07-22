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
public class LoginRequest implements Serializable {

    @Email(message = "email must be in valid format")
    @NotBlank(message = "email must be present")
    private String email;

    @NotBlank(message = "must be present")
    private String password;
}
