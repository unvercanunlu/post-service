package tr.unvercanunlu.microservices.postservice.model.entity;

import tr.unvercanunlu.microservices.postservice.config.DateConfig;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.function.Supplier;

public class PostHelper {

    public static Supplier<Post> generatePost = () ->
            Post.builder()
                    .id(UUID.randomUUID())
                    .author("author-1")
                    .content("content-1")
                    .viewCount(1L)
                    .postDate(ZonedDateTime.parse(LocalDateTime.now().format(DateConfig.DATE_TIME_FORMATTER), DateConfig.DATE_TIME_FORMATTER))
                    .build();
}
