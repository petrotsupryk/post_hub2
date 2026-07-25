package petro.post_hub2.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDto implements Serializable {
    private int id;
    private String title;
    private String context;
    private LocalDateTime created;
    private LocalDateTime updated;
}
