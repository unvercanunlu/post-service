package tr.unvercanunlu.postservice.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import tr.unvercanunlu.postservice.model.entity.Post;

import java.time.ZonedDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
class PostRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private IPostRepository postRepository;

    @BeforeAll
    void beforeAll() {
        this.postRepository.deleteAll();
    }

    @Test
    void whenCheckPostExistsById_whenPostExists_thenReturnTrue() {
        Post post = Post.builder()
                .author("author")
                .content("content")
                .viewCount(1)
                .postDate(ZonedDateTime.now())
                .build();

        post = this.entityManager.persist(post);

        Boolean postExists = this.postRepository.checkExistsById(post.getId());

        assertNotNull(postExists);
        assertTrue(postExists);
    }

    @Test
    void whenCheckPostExistsById_whenPostExists_thenReturnFalse() {
        UUID postId = UUID.randomUUID();

        Boolean postExists = this.postRepository.checkExistsById(postId);

        assertNotNull(postExists);
        assertFalse(postExists);
    }

}
