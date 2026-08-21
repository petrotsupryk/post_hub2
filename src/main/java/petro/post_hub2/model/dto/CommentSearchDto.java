package petro.post_hub2.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentSearchDto {
    private int id;
    private int userId;
    private int postId;
    private LocalDateTime created;
    private LocalDateTime updated;
    private boolean deleted;
}
