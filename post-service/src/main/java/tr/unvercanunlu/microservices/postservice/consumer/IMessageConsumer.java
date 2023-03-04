package tr.unvercanunlu.microservices.postservice.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface IMessageConsumer<K, V> {

    void onMessage(ConsumerRecord<K, V> payload);
}