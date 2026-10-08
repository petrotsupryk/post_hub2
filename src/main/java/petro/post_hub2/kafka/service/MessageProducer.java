package petro.post_hub2.kafka.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
@RequiredArgsConstructor
public class MessageProducer {
    private final JsonMapper jsonMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;
}
