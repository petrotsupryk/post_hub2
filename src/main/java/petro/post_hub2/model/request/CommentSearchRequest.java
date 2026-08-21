package petro.post_hub2.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import petro.post_hub2.model.enums.CommentSortField;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentSearchRequest implements Serializable {
    private String message;
    private int postId;
    private boolean deleted;
    private CommentSortField SortField;
}
