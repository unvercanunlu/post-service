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
import tr.unvercanunlu.microservices.postservice.producer.IMessageProducer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class MessageProducer implements IMessageProducer {

    public static final List<String> SENT_MESSAGE_IDS_BY_INSTANCE = new ArrayList<>();

    private final Logger logger = LoggerFactory.getLogger(MessageProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    @Value(value = "${spring.kafka.topic}")
    private String topic;

    public MessageProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void sendForUpsert(Post post) {
        String messageId = UUID.randomUUID().toString();

        String data;
        try {
            data = this.objectMapper.writeValueAsString(post);

        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
            return;
        }

        Message message = Message.builder()
                .action(Action.UPSERT)
                .data(data)
                .build();
        this.logger.info(message + " is prepared to send.");

        this.send(this.topic, messageId, message);
    }

    @Override
    public void sendForDelete(UUID postId) {
        String messageId = UUID.randomUUID().toString();
        String data = postId.toString();

        Message message = Message.builder()
                .action(Action.DELETE)
                .data(data)
                .build();
        this.logger.info(message + " is prepared to send.");

        this.send(this.topic, messageId, message);
    }

    private void send(String topic, String messageId, Message message) {
        String messageBody;
        try {
            messageBody = this.objectMapper.writeValueAsString(message);

        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
            return;
        }

        this.kafkaTemplate.send(topic, messageId, messageBody);
        this.logger.info(messageBody + " with " + messageId + " ID is sent.");

        SENT_MESSAGE_IDS_BY_INSTANCE.add(messageId);
    }
}
