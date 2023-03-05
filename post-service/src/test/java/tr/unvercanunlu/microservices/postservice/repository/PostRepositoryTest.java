package tr.unvercanunlu.microservices.postservice.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import tr.unvercanunlu.microservices.postservice.model.entity.Post;
import tr.unvercanunlu.microservices.postservice.model.entity.PostHelper;

import java.time.ZonedDateTime;
import java.util.List;
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
        Post post = PostHelper.generate.get();

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

    @Test
    void givenTop_whenGetTopOrderedByViewList_thenReturnListOfPostsOrderedByView() {
        Post post1 = PostHelper.generate.get();
        post1.setViewCount(1L);

        post1 = this.entityManager.persist(post1);

        Post post2 = PostHelper.generate.get();
        post2.setViewCount(2L);

        post2 = this.entityManager.persist(post2);

        List<Post> expectedPosts = List.of(post1, post2);

        Integer top = expectedPosts.size();

        List<Post> actualPosts = this.postRepository.getTopOrderedByViewList(top);

        assertNotNull(actualPosts);
        assertEquals(top, actualPosts.size());

        PostHelper.compare.accept(post2, actualPosts.get(0));

        PostHelper.compare.accept(post1, actualPosts.get(1));
    }

    @Test
    void givenTop_whenGetTopOrderedByDateList_thenReturnListOfPostsOrderedByDate() {
        Post post1 = PostHelper.generate.get();
        post1.setPostDate(ZonedDateTime.now().minusDays(2));

        post1 = this.entityManager.persist(post1);

        Post post2 = PostHelper.generate.get();
        post2.setPostDate(ZonedDateTime.now().minusDays(1));

        post2 = this.entityManager.persist(post2);

        List<Post> expectedPosts = List.of(post1, post2);

        Integer top = expectedPosts.size();

        List<Post> actualPosts = this.postRepository.getTopOrderedByDateList(top);

        assertNotNull(actualPosts);
        assertEquals(top, actualPosts.size());

        PostHelper.compare.accept(post2, actualPosts.get(0));

        PostHelper.compare.accept(post1, actualPosts.get(1));
    }
}
