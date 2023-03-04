package tr.unvercanunlu.microservices.postservice.producer;

import tr.unvercanunlu.microservices.postservice.model.entity.Post;

import java.util.UUID;

public interface IMessageProducer {

    void sendForUpsert(Post post);

    void sendForDelete(UUID postId);
}