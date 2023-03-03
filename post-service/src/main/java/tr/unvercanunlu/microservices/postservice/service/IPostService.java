package tr.unvercanunlu.microservices.postservice.service;

import tr.unvercanunlu.microservices.postservice.model.request.PostRequest;
import tr.unvercanunlu.microservices.postservice.model.response.PostDto;

import java.util.List;
import java.util.UUID;

public interface IPostService {

    List<PostDto> getAllPosts();

    PostDto getPost(UUID postId);

    void deletePost(UUID postId);

    PostDto createPost(PostRequest postRequest);

    PostDto updatePost(UUID postId, PostRequest postRequest);
}
