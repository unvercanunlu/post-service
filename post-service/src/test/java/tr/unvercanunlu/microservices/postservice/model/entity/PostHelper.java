package tr.unvercanunlu.microservices.postservice.model.entity;

import tr.unvercanunlu.microservices.postservice.config.DateConfig;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PostHelper {

    public static final Supplier<Post> generate = () ->
            Post.builder()
                    .id(UUID.randomUUID())
                    .author("author-1")
                    .content("content-1")
                    .viewCount(1L)
                    .postDate(LocalDateTime.parse(
                            LocalDateTime.now().format(DateConfig.DATE_TIME_FORMATTER),
                            DateConfig.DATE_TIME_FORMATTER).atZone(ZoneId.systemDefault()))
                    .build();

    public static final BiConsumer<Post, Post> compare = (expected, actual) ->
            Optional.ofNullable(expected).ifPresent(t -> {
                assertNotNull(actual);

                Optional.ofNullable(expected.getId()).ifPresent(x -> {
                    assertNotNull(actual.getId());
                    assertEquals(expected.getId(), actual.getId());
                });

                Optional.ofNullable(expected.getPostDate()).ifPresent(x -> {
                    assertNotNull(actual.getPostDate());
                    assertEquals(expected.getPostDate(), actual.getPostDate());
                });

                Optional.ofNullable(expected.getViewCount()).ifPresent(x -> {
                    assertNotNull(actual.getViewCount());
                    assertEquals(expected.getViewCount(), actual.getViewCount());
                });

                Optional.ofNullable(expected.getContent()).ifPresent(x -> {
                    assertNotNull(actual.getContent());
                    assertEquals(expected.getContent(), actual.getContent());
                });

                Optional.ofNullable(expected.getAuthor()).ifPresent(x -> {
                    assertNotNull(actual.getAuthor());
                    assertEquals(expected.getAuthor(), actual.getAuthor());
                });
            });

    public static final BiConsumer<UUID, UUID> comparePostId = (expected, actual) ->
            Optional.ofNullable(expected).ifPresent(t -> {
                assertNotNull(actual);
                assertEquals(expected, actual);
            });
}
