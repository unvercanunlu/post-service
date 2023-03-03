package tr.unvercanunlu.microservices.postservice.producer;

import tr.unvercanunlu.microservices.postservice.model.constant.Action;
import tr.unvercanunlu.microservices.postservice.model.entity.Post;

public interface IKafkaProducer {

    void send(Action action, Post message);
}