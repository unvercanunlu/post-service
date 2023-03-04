package tr.unvercanunlu.microservices.postservice.model.request;

import tr.unvercanunlu.microservices.postservice.config.DateConfig;

import java.time.LocalDateTime;
import java.util.function.Supplier;

public class PostRequestHelper {

    public static Supplier<PostRequest> generatePostRequest = () ->
            PostRequest.builder()
                    .author("author-1")
                    .content("content-1")
                    .viewCount(1L)
                    .postDate(LocalDateTime.parse(LocalDateTime.now().format(DateConfig.DATE_TIME_FORMATTER), DateConfig.DATE_TIME_FORMATTER))
                    .build();
}
