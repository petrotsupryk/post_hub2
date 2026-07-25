package petro.post_hub2.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import petro.post_hub2.model.enums.PostSortField;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostSearchRequest implements Serializable {
    private String title;
    private String content;
    private boolean deleted;
    private String keyword;
    private PostSortField postSortField;
}
