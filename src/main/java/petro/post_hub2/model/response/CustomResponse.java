package petro.post_hub2.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomResponse<T> implements Serializable {
    private String message;
    private T body;
    private boolean success;

    public static <T extends Serializable> CustomResponse<T> successWithToken(T body) {
        return new CustomResponse<>("token created successfully", body, true);
    }

    public static <T extends Serializable> CustomResponse<T> success(T body) {
        return new CustomResponse<>("success", body, true);
    }
}
