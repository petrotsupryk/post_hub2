package petro.post_hub2.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "posts")
public class Post implements Serializable {
    private static final String ID_FIELD_NAME = "id";
    private static final String USER_ID_FIELD_NAME = "userId";
    private static final String TITLE_FIELD_NAME = "title";
    private static final String CONTEXT_FIELD_NAME = "context";
    private static final String UPDATED_FIELD_NAME = "updated";
    private static final String DELETED_FIELD_NAME = "deleted";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "created")
    private LocalDateTime created = LocalDateTime.now();

    @Column(name = "updated")
    private LocalDateTime updated = LocalDateTime.now();

    @Column(name = "deleted")
    private boolean deleted = false;
}
