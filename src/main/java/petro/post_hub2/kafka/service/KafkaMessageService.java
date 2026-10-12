package petro.post_hub2.kafka.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import petro.post_hub2.kafka.model.ActionType;
import petro.post_hub2.kafka.model.PriorityType;
import petro.post_hub2.kafka.model.UtilMessage;

@Service
@AllArgsConstructor
public class KafkaMessageService {
    private final MessageProducer messageProducer;

    public void sendPostCreateLog(int postId, int userId) {
        UtilMessage utilMessage = UtilMessage.builder()
                .userId(userId)
                .message("user with id: " + userId + " create post with id: " + postId)
                .actionType(ActionType.CREATE)
                .priorityType(PriorityType.MEDIUM)
                .build();
        messageProducer.sendLog(utilMessage);
    }

    public void sendPostGetLog(int postId) {
        UtilMessage utilMessage = UtilMessage.builder()
                .message("user get post with id: " + postId)
                .actionType(ActionType.GET)
                .priorityType(PriorityType.LOW)
                .build();
        messageProducer.sendLog(utilMessage);
    }

    public void sendPostUpdateLog(int postId, int userId) {
        UtilMessage utilMessage = UtilMessage.builder()
                .message("user with id: " + userId + "update post with id: " + postId)
                .actionType(ActionType.UPDATE)
                .priorityType(PriorityType.MEDIUM)
                .build();
        messageProducer.sendLog(utilMessage);
    }

    public void sendPostDeleLog(int postId, int userId) {
        UtilMessage utilMessage = UtilMessage.builder()
                .message("user with id: " + userId + " delete post with id: " + postId)
                .actionType(ActionType.DELETE)
                .priorityType(PriorityType.MEDIUM)
                .build();
        messageProducer.sendLog(utilMessage);
    }

    public void sendPostGetAllByPagination() {
        UtilMessage utilMessage = UtilMessage.builder()
                .message("find all post with Pagination")
                .actionType(ActionType.GET)
                .priorityType(PriorityType.LOW)
                .build();
        messageProducer.sendLog(utilMessage);
    }

    public void sendPostGetAllByPaginationAndParam() {
        UtilMessage utilMessage = UtilMessage.builder()
                .message("find all post with Pagination and Param")
                .actionType(ActionType.GET)
                .priorityType(PriorityType.LOW)
                .build();
        messageProducer.sendLog(utilMessage);
    }
}
