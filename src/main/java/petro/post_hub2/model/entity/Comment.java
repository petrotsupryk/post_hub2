package petro.post_hub2.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Email
@Table(name = "comments")
public class Comment {
    public final static String ID_FIELD_NAME = "id";
    public final static String MESSAGE_FIELD_NAME = "message";
    public final static String CREATED_FIELD_NAME = "created";
    public final static String UPDATED_FIELD_NAME = "updated";
    public final static String DELETED_FIELD_NAME = "deleted";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "message")
    private String message;

    @Column(name = "created")
    private LocalDateTime created = LocalDateTime.now();

    @Column(name = "updated")
    private LocalDateTime updated = LocalDateTime.now();

    @Column(name = "deleted")
    private boolean deleted = false;
}
