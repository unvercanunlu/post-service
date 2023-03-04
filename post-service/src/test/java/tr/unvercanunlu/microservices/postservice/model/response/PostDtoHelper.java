package tr.unvercanunlu.microservices.postservice.model.response;

import tr.unvercanunlu.microservices.postservice.config.DateConfig;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Supplier;

public class PostDtoHelper {

    public static Supplier<PostDto> generatePostDto = () ->
            PostDto.builder()
                    .id(UUID.randomUUID())
                    .author("author-1")
                    .content("content-1")
                    .viewCount(1L)
                    .postDate(LocalDateTime.parse(LocalDateTime.now().format(DateConfig.DATE_TIME_FORMATTER), DateConfig.DATE_TIME_FORMATTER))
                    .build();
}
