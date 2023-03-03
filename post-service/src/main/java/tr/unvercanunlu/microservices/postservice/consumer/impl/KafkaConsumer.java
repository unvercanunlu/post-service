package tr.unvercanunlu.microservices.postservice.consumer.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tr.unvercanunlu.microservices.postservice.consumer.IKafkaConsumer;
import tr.unvercanunlu.microservices.postservice.model.Message;
import tr.unvercanunlu.microservices.postservice.model.constant.Action;
import tr.unvercanunlu.microservices.postservice.producer.impl.KafkaProducer;
import tr.unvercanunlu.microservices.postservice.repository.IPostRepository;

import java.util.UUID;

@Component
public class KafkaConsumer implements IKafkaConsumer<String, String> {

    private final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    private final IPostRepository postRepository;

    private final ObjectMapper objectMapper;

    public KafkaConsumer(IPostRepository postRepository, ObjectMapper objectMapper) {
        this.postRepository = postRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    @KafkaListener(topics = "${spring.kafka.topic}", containerFactory = "listenerFactory")
    public void onMessage(ConsumerRecord<String, String> payload) throws JsonProcessingException {
        String messageId = payload.key();
        String messageBody = payload.value();
        logger.info("Message " + messageBody + " with " + messageId + " ID is received.");

        Message message = this.objectMapper.readValue(messageBody, Message.class);

        UUID id = UUID.fromString(messageId);
        if (KafkaProducer.sentByMe.contains(id)) {
            return;
        }

        if (message.getAction().getCode().equals(Action.DELETE.getCode())) {
            this.postRepository.delete(message.getPost());
            logger.info(message.getPost() + " is deleted from database.");
        } else if (message.getAction().getCode().equals(Action.UPSERT.getCode())) {
            this.postRepository.save(message.getPost());
            logger.info(message.getPost() + " is saved in database.");
        } else {
            logger.info("Unknown action: " + message.getAction().getCode());
        }
    }
}
