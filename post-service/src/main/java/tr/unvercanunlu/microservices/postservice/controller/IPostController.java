package tr.unvercanunlu.microservices.postservice.controller;

import org.springframework.http.ResponseEntity;
import tr.unvercanunlu.microservices.postservice.model.request.PostRequest;
import tr.unvercanunlu.microservices.postservice.model.response.PostDto;

import java.util.List;
import java.util.UUID;

public interface IPostController {

    ResponseEntity<List<PostDto>> getAllPosts();

    ResponseEntity<PostDto> getPost(UUID postId);

    ResponseEntity<Void> deletePost(UUID postId);

    ResponseEntity<PostDto> createPost(PostRequest postRequest);

    ResponseEntity<PostDto> updatePost(UUID postId, PostRequest postRequest);
}
