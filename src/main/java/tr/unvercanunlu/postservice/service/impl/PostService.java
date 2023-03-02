package tr.unvercanunlu.postservice.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.unvercanunlu.postservice.exception.PostNotFoundException;
import tr.unvercanunlu.postservice.model.entity.Post;
import tr.unvercanunlu.postservice.model.request.PostRequest;
import tr.unvercanunlu.postservice.model.response.PostDto;
import tr.unvercanunlu.postservice.repository.IPostRepository;
import tr.unvercanunlu.postservice.service.IPostService;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

@Service
public class PostService implements IPostService {

    private final Function<Post, PostDto> postToPostDtoMapper = post ->
            PostDto.builder()
                    .id(post.getId())
                    .author(post.getAuthor())
                    .content(post.getContent())
                    .postDate(post.getPostDate())
                    .viewCount(post.getViewCount())
                    .build();

    private final Function<PostRequest, Post> postRequestToPostMapper = postRequest ->
            Post.builder()
                    .author(postRequest.getAuthor())
                    .content(postRequest.getContent())
                    .postDate(postRequest.getPostDate())
                    .viewCount(postRequest.getViewCount())
                    .build();

    private final BiFunction<Post, PostRequest, Post> postWithPostRequestToPostUpdater = (post, postRequest) -> {
        post.setContent(postRequest.getContent());
        post.setAuthor(postRequest.getAuthor());
        post.setPostDate(postRequest.getPostDate());
        post.setViewCount(post.getViewCount());
        return post;
    };

    private final IPostRepository postRepository;

    public PostService(IPostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostDto> getAllPosts() {
        List<Post> posts = this.postRepository.findAll();
        return posts.stream()
                .map(this.postToPostDtoMapper)
                .sorted(Comparator.nullsLast(Comparator.comparing(PostDto::getPostDate).reversed()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PostDto getPost(UUID postId) {
        Post post = this.postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));
        return this.postToPostDtoMapper.apply(post);
    }

    @Override
    @Transactional
    public void deletePost(UUID postId) {
        boolean postExists = this.postRepository.checkExistsById(postId);
        if (postExists) {
            this.postRepository.deleteById(postId);
        } else {
            throw new PostNotFoundException(postId);
        }
    }

    @Override
    @Transactional
    public PostDto createPost(PostRequest postRequest) {
        Post post = this.postRequestToPostMapper.apply(postRequest);
        this.postRepository.save(post);
        return this.postToPostDtoMapper.apply(post);
    }

    @Override
    @Transactional
    public PostDto updatePost(UUID postId, PostRequest postRequest) {
        Post post = this.postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));
        post = this.postWithPostRequestToPostUpdater.apply(post, postRequest);
        this.postRepository.save(post);
        return this.postToPostDtoMapper.apply(post);
    }

}
