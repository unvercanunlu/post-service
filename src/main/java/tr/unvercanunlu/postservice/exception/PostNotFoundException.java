package tr.unvercanunlu.postservice.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@Builder
public class PostNotFoundException extends RuntimeException implements Serializable {

    private final UUID postId;

    public PostNotFoundException(UUID postId) {
        this.postId = postId;
    }

}
