package petro.post_hub2.kafka.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Data
@Builder
public class UtilMessage implements Serializable {
    private Integer userId;
    private String message;
    private ActionType actionType;
    private PriorityType priorityType;
    private Service service;
}
