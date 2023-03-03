package tr.unvercanunlu.microservices.postservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tr.unvercanunlu.microservices.postservice.model.entity.Post;

import java.util.UUID;

@Repository
public interface IPostRepository extends JpaRepository<Post, UUID> {

    @Query(value = "SELECT CASE " +
            "WHEN count(post) > 0 THEN true ELSE false END " +
            "from Post post " +
            "where post.id = :postId")
    Boolean checkExistsById(UUID postId);
}
