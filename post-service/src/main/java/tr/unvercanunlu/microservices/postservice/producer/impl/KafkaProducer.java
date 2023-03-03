package tr.unvercanunlu.microservices.postservice.producer.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tr.unvercanunlu.microservices.postservice.model.Message;
import tr.unvercanunlu.microservices.postservice.model.constant.Action;
import tr.unvercanunlu.microservices.postservice.model.entity.Post;
import tr.unvercanunlu.microservices.postservice.producer.IKafkaProducer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class KafkaProducer implements IKafkaProducer {

    public static final List<UUID> sentByMe = new ArrayList<>();
    private final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    @Value(value = "${spring.kafka.topic}")
    private String topic;

    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void send(Action action, Post post) {
        UUID messageId = UUID.randomUUID();
        try {
            Message message = Message.builder()
                    .action(action)
                    .post(post)
                    .build();
            String messageJson = this.objectMapper.writeValueAsString(message);
            this.kafkaTemplate.send(this.topic, messageId.toString(), messageJson);
            logger.info("Message " + message + " with " + messageId + " ID is sent");
            sentByMe.add(messageId);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
