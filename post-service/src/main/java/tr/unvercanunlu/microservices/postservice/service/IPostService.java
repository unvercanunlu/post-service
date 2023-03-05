package tr.unvercanunlu.microservices.postservice.service;

import tr.unvercanunlu.microservices.postservice.model.constant.Order;
import tr.unvercanunlu.microservices.postservice.model.request.PostRequest;
import tr.unvercanunlu.microservices.postservice.model.response.PostDto;

import java.util.List;
import java.util.UUID;

public interface IPostService {

    List<PostDto> getTopOrderedPosts(Order order, Integer top);

    PostDto getPost(UUID postId);

    void deletePost(UUID postId);

    PostDto createPost(PostRequest postRequest);

    PostDto updatePost(UUID postId, PostRequest postRequest);

    void checkExistsPost(UUID postId);

    // PostDto updatePartialPost(UUID postId, PostRequest postRequest);
}
