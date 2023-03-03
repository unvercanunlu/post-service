package tr.unvercanunlu.microservices.postservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface IKafkaConsumer<K, V> {

    void onMessage(ConsumerRecord<K, V> payload) throws JsonProcessingException;
}