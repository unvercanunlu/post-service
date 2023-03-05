package tr.unvercanunlu.microservices.postservice.consumer.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tr.unvercanunlu.microservices.postservice.consumer.IMessageConsumer;
import tr.unvercanunlu.microservices.postservice.model.Message;
import tr.unvercanunlu.microservices.postservice.model.constant.Action;
import tr.unvercanunlu.microservices.postservice.model.entity.Post;
import tr.unvercanunlu.microservices.postservice.producer.impl.MessageProducer;
import tr.unvercanunlu.microservices.postservice.repository.IPostRepository;

import java.util.Optional;
import java.util.UUID;

@Component
public class MessageConsumer implements IMessageConsumer<String, String> {

    private final Logger logger = LoggerFactory.getLogger(MessageConsumer.class);

    private final IPostRepository postRepository;

    private final ObjectMapper objectMapper;

    public MessageConsumer(IPostRepository postRepository, ObjectMapper objectMapper) {
        this.postRepository = postRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    @KafkaListener(topics = "${spring.kafka.topic}", containerFactory = "listenerFactory", groupId = "${spring.kafka.group-id}")
    public void onMessage(ConsumerRecord<String, String> payload) {
        String messageId = payload.key();

        if (MessageProducer.SENT_MESSAGE_IDS_BY_INSTANCE.contains(messageId)) {
            this.logger.info(messageId + " ID is already sent by this instance, so ignored.");
            return;
        }

        String messageBody = payload.value();
        this.logger.info(messageBody + " with " + messageId + " ID is received.");

        Message message;
        try {
            message = this.objectMapper.readValue(messageBody, Message.class);

        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
            return;
        }

        Optional.ofNullable(message).ifPresent(m -> this.processMessage(message));
    }

    private void processMessage(Message message) {
        Optional.ofNullable(message.getAction())
                .ifPresent(action -> {
                    if (action.getCode().equals(Action.DELETE.getCode())) {
                        this.processDeleteMessage(message);

                    } else if (action.getCode().equals(Action.UPSERT.getCode())) {
                        this.processUpsertMessage(message);

                    } else this.logger.info("Unimplemented message action: " + action.getCode());
                });
    }

    private void processDeleteMessage(Message message) {
        String postId = message.getData();
        this.postRepository.deleteById(UUID.fromString(postId));
        this.logger.info("Post with " + postId + " ID is deleted from database.");
    }

    private void processUpsertMessage(Message message) {
        String postJson = message.getData();

        Post post;
        try {
            post = this.objectMapper.readValue(postJson, Post.class);

        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
            return;
        }

        post = this.postRepository.save(post);
        this.logger.info(post + " is saved in database.");
    }
}
