package tr.unvercanunlu.microservices.postservice.model.request;

import tr.unvercanunlu.microservices.postservice.config.DateConfig;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PostRequestHelper {

    public static final BiConsumer<PostRequest, PostRequest> compare = (expected, actual) ->
            Optional.ofNullable(expected).ifPresent(t -> {
                assertNotNull(actual);

                Optional.ofNullable(expected.getPostDate()).ifPresent(x -> {
                    assertNotNull(actual.getPostDate());
                    assertEquals(expected.getPostDate(), actual.getPostDate());
                });

                Optional.ofNullable(expected.getAuthor()).ifPresent(x -> {
                    assertNotNull(actual.getAuthor());
                    assertEquals(expected.getAuthor(), actual.getAuthor());
                });

                Optional.ofNullable(expected.getContent()).ifPresent(x -> {
                    assertNotNull(actual.getContent());
                    assertEquals(expected.getContent(), actual.getContent());
                });

                Optional.ofNullable(expected.getViewCount()).ifPresent(x -> {
                    assertNotNull(actual.getViewCount());
                    assertEquals(expected.getViewCount(), actual.getViewCount());
                });
            });
    public static Supplier<PostRequest> generate = () ->
            PostRequest.builder()
                    .author("author-1")
                    .content("content-1")
                    .viewCount(1L)
                    .postDate(LocalDateTime.parse(
                            LocalDateTime.now().format(DateConfig.DATE_TIME_FORMATTER),
                            DateConfig.DATE_TIME_FORMATTER))
                    .build();
}
