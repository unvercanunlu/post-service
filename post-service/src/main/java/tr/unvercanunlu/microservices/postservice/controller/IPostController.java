package tr.unvercanunlu.microservices.postservice.controller;

import org.springframework.http.ResponseEntity;
import tr.unvercanunlu.microservices.postservice.model.request.PostRequest;
import tr.unvercanunlu.microservices.postservice.model.response.PostDto;

import java.util.List;
import java.util.UUID;

public interface IPostController {

    ResponseEntity<List<PostDto>> getTopOrderedPosts(String orderName, Integer top);

    ResponseEntity<PostDto> getPost(UUID postId);

    ResponseEntity<Void> deletePost(UUID postId);

    ResponseEntity<PostDto> createPost(PostRequest postRequest);

    ResponseEntity<PostDto> updatePost(UUID postId, PostRequest postRequest);

    ResponseEntity<Void> checkExistsPost(UUID postId);

    // ResponseEntity<PostDto> updatePartialPost(UUID postId, PostRequest postRequest);
}
