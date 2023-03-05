package tr.unvercanunlu.microservices.postservice.exception;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@ToString
public class PostNotFoundException extends RuntimeException implements Serializable {

    private final UUID postId;

    public PostNotFoundException(UUID postId) {
        super("Post with " + postId + " ID  is not found.");
        this.postId = postId;
    }
}
