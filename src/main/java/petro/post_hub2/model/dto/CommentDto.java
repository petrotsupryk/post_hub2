package petro.post_hub2.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto implements Serializable {
    private int id;
    private String message;
    private int ownerId;
    private int postId;
    private LocalDateTime created;
    private LocalDateTime updated;
}
