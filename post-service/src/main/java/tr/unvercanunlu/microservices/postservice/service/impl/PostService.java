package tr.unvercanunlu.microservices.postservice.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.unvercanunlu.microservices.postservice.exception.PostNotFoundException;
import tr.unvercanunlu.microservices.postservice.model.entity.Post;
import tr.unvercanunlu.microservices.postservice.model.request.PostRequest;
import tr.unvercanunlu.microservices.postservice.model.response.PostDto;
import tr.unvercanunlu.microservices.postservice.repository.IPostRepository;
import tr.unvercanunlu.microservices.postservice.service.IPostService;

import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

@Service
public class PostService implements IPostService {

    private final Logger logger = LoggerFactory.getLogger(PostService.class);

    private final Function<Post, PostDto> postToPostDtoMapper = post ->
            PostDto.builder()
                    .id(post.getId())
                    .author(post.getAuthor())
                    .content(post.getContent())
                    .postDate(post.getPostDate().toLocalDateTime())
                    .viewCount(post.getViewCount())
                    .build();

    private final Function<PostRequest, Post> postRequestToPostMapper = postRequest ->
            Post.builder()
                    .author(postRequest.getAuthor())
                    .content(postRequest.getContent())
                    .postDate(postRequest.getPostDate().atZone(ZoneId.systemDefault()))
                    .viewCount(postRequest.getViewCount())
                    .build();

    private final BiFunction<Post, PostRequest, Post> postWithPostRequestToPostUpdater = (post, postRequest) -> {
        post.setContent(postRequest.getContent());
        post.setAuthor(postRequest.getAuthor());
        post.setPostDate(postRequest.getPostDate().atZone(ZoneId.systemDefault()));
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
        logger.info(posts + " are obtained from database.");
        List<PostDto> postDtos = posts.stream().map(this.postToPostDtoMapper).toList();
        logger.info(posts + " are mapped to " + postDtos);
        postDtos = postDtos.stream().sorted(Comparator.nullsLast(Comparator.comparing(PostDto::getPostDate).reversed())).toList();
        logger.info(postDtos + " are sorted by post date.");
        return postDtos;
    }

    @Override
    @Transactional(readOnly = true)
    public PostDto getPost(UUID postId) {
        Post post = this.postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));
        logger.info(post + " is obtained from database.");
        PostDto postDto = this.postToPostDtoMapper.apply(post);
        logger.info(post + " is mapped to " + postDto);
        return postDto;
    }

    @Override
    @Transactional
    public void deletePost(UUID postId) {
        boolean postExists = this.postRepository.checkExistsById(postId);
        logger.info("Post with " + postId + " ID is checked whether post exists in database or not.");
        if (postExists) {
            this.postRepository.deleteById(postId);
            logger.info("Post with " + postId + " ID is deleted from database.");
        } else {
            logger.info("Post with " + postId + " ID does not exist in database.");
            throw new PostNotFoundException(postId);
        }
    }

    @Override
    @Transactional
    public PostDto createPost(PostRequest postRequest) {
        Post post = this.postRequestToPostMapper.apply(postRequest);
        logger.info(postRequest + " is mapped to " + post);
        this.postRepository.save(post);
        logger.info(post + " is created in database.");
        PostDto postDto = this.postToPostDtoMapper.apply(post);
        logger.info(post + " is mapped to " + postDto);
        return postDto;
    }

    @Override
    @Transactional
    public PostDto updatePost(UUID postId, PostRequest postRequest) {
        Post post = this.postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));
        logger.info(post + " is obtained from database.");
        post = this.postWithPostRequestToPostUpdater.apply(post, postRequest);
        logger.info(post + " is updated to " + post + " with " + postRequest);
        this.postRepository.save(post);
        logger.info(post + " is updated in database.");
        PostDto postDto = this.postToPostDtoMapper.apply(post);
        logger.info(post + " is mapped to " + postDto);
        return postDto;
    }
}
