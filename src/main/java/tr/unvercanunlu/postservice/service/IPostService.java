package tr.unvercanunlu.postservice.service;

import tr.unvercanunlu.postservice.model.request.PostRequest;
import tr.unvercanunlu.postservice.model.response.PostDto;

import java.util.List;
import java.util.UUID;

public interface IPostService {

    List<PostDto> getAllPosts();

    PostDto getPost(UUID postId);

    void deletePost(UUID postId);

    PostDto createPost(PostRequest postRequest);

    PostDto updatePost(UUID postId, PostRequest postRequest);
}
