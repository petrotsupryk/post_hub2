package petro.post_hub2.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewPostRequest implements Serializable {
    @NotBlank(message = "title must be present")
    @Size(min = 5, max = 50, message = "title must be from 5 to 50")
    private String title;

    @NotBlank(message = "context must be present")
    @Size(min = 5, max = 50, message = "context must be from 5 to 100")
    private String content;
}
