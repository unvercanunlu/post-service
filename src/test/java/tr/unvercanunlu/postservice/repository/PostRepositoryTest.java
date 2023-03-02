package tr.unvercanunlu.postservice.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import tr.unvercanunlu.postservice.model.entity.Post;

import java.time.ZonedDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PostRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private IPostRepository postRepository;

    @AfterEach
    void tearDown() {
        this.entityManager.flush();
    }

    @Test
    void givenPostId_whenPostDoesExists_whenCheckPostExistsById_thenReturnTrue() {
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
    void givenPostId_whenPostDoesNotExist_whenCheckPostExistsById_thenReturnFalse() {
        UUID postId = UUID.randomUUID();

        Boolean postExists = this.postRepository.checkExistsById(postId);

        assertNotNull(postExists);
        assertFalse(postExists);
    }

}
