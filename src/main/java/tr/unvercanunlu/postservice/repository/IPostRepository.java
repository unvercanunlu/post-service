package tr.unvercanunlu.postservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tr.unvercanunlu.postservice.model.entity.Post;

import java.util.UUID;

@Repository
public interface IPostRepository extends JpaRepository<Post, UUID> {
}
