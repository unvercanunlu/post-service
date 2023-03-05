package tr.unvercanunlu.microservices.postservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tr.unvercanunlu.microservices.postservice.model.entity.Post;

import java.util.List;
import java.util.UUID;

@Repository
public interface IPostRepository extends JpaRepository<Post, UUID> {

    @Query(value = "SELECT CASE " +
            "WHEN count(post) > 0 THEN true ELSE false END " +
            "from Post post " +
            "where post.id = :postId")
    Boolean checkExistsById(UUID postId);

    @Query(value = "SELECT post " +
            "FROM Post post " +
            "ORDER BY post.viewCount DESC " +
            "LIMIT :top")
    List<Post> getTopOrderedByViewList(@Param(value = "top") Integer top);

    @Query(value = "SELECT post " +
            "FROM Post post " +
            "ORDER BY post.postDate DESC " +
            "LIMIT :top")
    List<Post> getTopOrderedByDateList(@Param(value = "top") Integer top);
}
