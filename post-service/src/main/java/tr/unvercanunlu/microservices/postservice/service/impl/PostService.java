package tr.unvercanunlu.microservices.postservice.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.unvercanunlu.microservices.postservice.exception.OrderNotSuitableException;
import tr.unvercanunlu.microservices.postservice.exception.PostNotFoundException;
import tr.unvercanunlu.microservices.postservice.model.constant.Order;
import tr.unvercanunlu.microservices.postservice.model.entity.Post;
import tr.unvercanunlu.microservices.postservice.model.request.PostRequest;
import tr.unvercanunlu.microservices.postservice.model.response.PostDto;
import tr.unvercanunlu.microservices.postservice.producer.IMessageProducer;
import tr.unvercanunlu.microservices.postservice.repository.IPostRepository;
import tr.unvercanunlu.microservices.postservice.service.IPostService;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    /*

    private final BiFunction<Post, PostRequest, Post> postWithPostRequestToPostPartialUpdater = (post, postRequest) -> {
        Optional.ofNullable(postRequest.getContent()).ifPresent(post::setContent);
        Optional.ofNullable(postRequest.getAuthor()).ifPresent(post::setAuthor);
        Optional.ofNullable(postRequest.getPostDate()).ifPresent(postDate -> postRequest.getPostDate().atZone(ZoneId.systemDefault()));
        Optional.ofNullable(postRequest.getViewCount()).ifPresent(post::setViewCount);
        return post;
    };

    */

    private final IPostRepository postRepository;

    private final IMessageProducer kafkaProducer;

    public PostService(IPostRepository postRepository, IMessageProducer kafkaProducer) {
        this.postRepository = postRepository;
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostDto> getTopOrderedPosts(Order order, Integer top) {
        Optional.ofNullable(order).orElseThrow(() -> new OrderNotSuitableException(String.valueOf(order)));

        List<Post> posts = new ArrayList<>();
        if (order.equals(Order.VIEW)) {
            posts = this.postRepository.getTopOrderedByViewList(top);
            this.logger.info(posts + " are obtained from database ordered by view.");

        } else if (order.equals(Order.DATE)) {
            posts = this.postRepository.getTopOrderedByDateList(top);
            this.logger.info(posts + " are obtained from database ordered by date.");
        }

        List<PostDto> postDtos = posts.stream().map(this.postToPostDtoMapper).toList();
        this.logger.info(posts + " are mapped to " + postDtos + " .");

        return postDtos;
    }

    @Override
    @Transactional(readOnly = true)
    public PostDto getPost(UUID postId) {
        Post post = this.postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));
        this.logger.info(post + " is obtained from database.");

        PostDto postDto = this.postToPostDtoMapper.apply(post);
        this.logger.info(post + " is mapped to " + postDto + " .");

        return postDto;
    }

    @Override
    @Transactional
    public void deletePost(UUID postId) {
        this.checkExistsPost(postId);

        this.postRepository.deleteById(postId);
        this.logger.info("Post with " + postId + " ID is deleted from database.");

        this.kafkaProducer.sendForDelete(postId);
    }

    @Override
    @Transactional
    public PostDto createPost(PostRequest postRequest) {
        Post post = this.postRequestToPostMapper.apply(postRequest);
        this.logger.info(postRequest + " is mapped to " + post + " .");

        post = this.postRepository.save(post);
        this.logger.info(post + " is created in database.");

        this.kafkaProducer.sendForUpsert(post);

        PostDto postDto = this.postToPostDtoMapper.apply(post);
        this.logger.info(post + " is mapped to " + postDto + " .");

        return postDto;
    }

    @Override
    @Transactional
    public PostDto updatePost(UUID postId, PostRequest postRequest) {
        Post post = this.postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));
        this.logger.info(post + " is obtained from database.");

        post = this.postWithPostRequestToPostUpdater.apply(post, postRequest);
        this.logger.info(post + " is updated to " + post + " with " + postRequest + " .");

        post = this.postRepository.save(post);
        this.logger.info(post + " is updated in database.");

        this.kafkaProducer.sendForUpsert(post);

        PostDto postDto = this.postToPostDtoMapper.apply(post);
        this.logger.info(post + " is mapped to " + postDto + " .");

        return postDto;
    }

    @Override
    public void checkExistsPost(UUID postId) {
        boolean postExists = this.postRepository.checkExistsById(postId);
        this.logger.info("Post with " + postId + " ID is checked whether post exists in database or not.");

        if (!postExists) {
            this.logger.info("Post with " + postId + " ID does not exist in database.");
            throw new PostNotFoundException(postId);
        }
    }

    /*

    @Override
    public PostDto updatePartialPost(UUID postId, PostRequest postRequest) {
        Post post = this.postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));
        this.logger.info(post + " is obtained from database.");

        post = this.postWithPostRequestToPostPartialUpdater.apply(post, postRequest);
        this.logger.info(post + " is partial updated to " + post + " with " + postRequest + " .");

        post = this.postRepository.save(post);
        this.logger.info(post + " is updated in database.");

        this.kafkaProducer.sendForUpsert(post);

        PostDto postDto = this.postToPostDtoMapper.apply(post);
        this.logger.info(post + " is mapped to " + postDto + " .");

        return postDto;
    }

    */
}
