package tr.unvercanunlu.microservices.postservice.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tr.unvercanunlu.microservices.postservice.exception.PostNotFoundException;
import tr.unvercanunlu.microservices.postservice.model.entity.Post;
import tr.unvercanunlu.microservices.postservice.model.entity.PostHelper;
import tr.unvercanunlu.microservices.postservice.model.request.PostRequest;
import tr.unvercanunlu.microservices.postservice.model.request.PostRequestHelper;
import tr.unvercanunlu.microservices.postservice.model.response.PostDto;
import tr.unvercanunlu.microservices.postservice.model.response.PostDtoHelper;
import tr.unvercanunlu.microservices.postservice.producer.impl.MessageProducer;
import tr.unvercanunlu.microservices.postservice.repository.IPostRepository;

import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(value = MockitoExtension.class)
class PostServiceTest {

    @Mock
    private IPostRepository postRepository;

    private final ArgumentCaptor<UUID> postIdCaptor = ArgumentCaptor.forClass(UUID.class);

    @InjectMocks
    private PostService postService;
    @Mock
    private MessageProducer messageProducer;

    private final ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);

    @Test
    void whenGetAllPosts_thenReturnListOfPostDtos() {
        Post post = PostHelper.generate.get();

        List<Post> posts = List.of(post);

        PostDto expectedPostDto = PostDto.builder()
                .id(post.getId())
                .author(post.getAuthor())
                .content(post.getContent())
                .viewCount(post.getViewCount())
                .postDate(post.getPostDate().toLocalDateTime())
                .build();

        List<PostDto> expectedPostDtos = List.of(expectedPostDto);

        when(this.postRepository.findAll()).thenReturn(posts);

        List<PostDto> actualPostDtos = this.postService.getAllPosts();

        verify(this.postRepository, times(1)).findAll();

        assertNotNull(actualPostDtos);
        assertEquals(expectedPostDtos.size(), actualPostDtos.size());

        PostDto actualPostDto = actualPostDtos.get(0);

        PostDtoHelper.compare.accept(expectedPostDto, actualPostDto);
    }

    @Test
    void givenPostId_whenPostExists_whenGetPost_thenReturnPostDto() {
        Post post = PostHelper.generate.get();

        PostDto expectedPostDto = PostDto.builder()
                .id(post.getId())
                .author(post.getAuthor())
                .content(post.getContent())
                .viewCount(post.getViewCount())
                .postDate(post.getPostDate().toLocalDateTime())
                .build();

        when(this.postRepository.findById(post.getId())).thenReturn(Optional.of(post));

        PostDto actualPostDto = this.postService.getPost(post.getId());

        PostDtoHelper.compare.accept(expectedPostDto, actualPostDto);

        verify(this.postRepository, times(1)).findById(this.postIdCaptor.capture());

        PostHelper.comparePostId.accept(post.getId(), this.postIdCaptor.getValue());
    }

    @Test
    void givenPostId_whenPostDoesNotExist_whenGetPost_thenThrowPostNotFoundException() {
        UUID postId = UUID.randomUUID();

        when(this.postRepository.findById(postId)).thenReturn(Optional.empty());

        try {
            this.postService.getPost(postId);

        } catch (PostNotFoundException ex) {
            verify(this.postRepository, times(1)).findById(this.postIdCaptor.capture());

            PostHelper.comparePostId.accept(postId, this.postIdCaptor.getValue());
        }
    }

    @Test
    void givenPostId_whenDeletePost() {
        Post post = PostHelper.generate.get();

        when(this.postRepository.checkExistsById(post.getId())).thenReturn(true);

        doNothing().when(this.messageProducer).sendForDelete(post.getId());

        this.postService.deletePost(post.getId());

        verify(this.postRepository, times(1)).checkExistsById(this.postIdCaptor.capture());

        PostHelper.comparePostId.accept(post.getId(), this.postIdCaptor.getValue());

        verify(this.messageProducer, times(1)).sendForDelete(this.postIdCaptor.capture());

        PostHelper.comparePostId.accept(post.getId(), this.postIdCaptor.getValue());
    }

    @Test
    void givenPostId_whenPostDoesNotExist_whenDeletePost_thenThrowPostNotFoundException() {
        UUID postId = UUID.randomUUID();

        when(this.postRepository.checkExistsById(postId)).thenReturn(false);

        try {
            this.postService.deletePost(postId);

        } catch (PostNotFoundException ex) {
            verify(this.postRepository, times(0)).deleteById(any(UUID.class));

            verify(this.messageProducer, times(0)).sendForDelete(any(UUID.class));

            verify(this.postRepository, times(1)).checkExistsById(this.postIdCaptor.capture());

            PostHelper.comparePostId.accept(postId, this.postIdCaptor.getValue());
        }
    }

    @Test
    void givenPostRequest_whenCreatePost_thenReturnPostDto() {
        PostRequest postRequest = PostRequestHelper.generate.get();

        Post postBeforeSave = Post.builder()
                .author(postRequest.getAuthor())
                .content(postRequest.getContent())
                .viewCount(postRequest.getViewCount())
                .postDate(postRequest.getPostDate().atZone(ZoneId.systemDefault()))
                .build();

        Post post = Post.builder()
                .id(UUID.randomUUID())
                .author(postRequest.getAuthor())
                .content(postRequest.getContent())
                .viewCount(postRequest.getViewCount())
                .postDate(postRequest.getPostDate().atZone(ZoneId.systemDefault()))
                .build();

        PostDto expectedPostDto = PostDto.builder()
                .id(post.getId())
                .author(post.getAuthor())
                .content(post.getContent())
                .viewCount(post.getViewCount())
                .postDate(post.getPostDate().toLocalDateTime())
                .build();

        when(this.postRepository.save(any(Post.class))).thenReturn(post);

        doNothing().when(this.messageProducer).sendForUpsert(post);

        PostDto actualPostDto = this.postService.createPost(postRequest);

        PostDtoHelper.compare.accept(expectedPostDto, actualPostDto);

        verify(this.postRepository, times(1)).save(this.postCaptor.capture());

        PostHelper.compare.accept(postBeforeSave, this.postCaptor.getValue());

        verify(this.messageProducer, times(1)).sendForUpsert(this.postCaptor.capture());

        PostHelper.compare.accept(post, this.postCaptor.getValue());
    }

    @Test
    void givenPostRequestAndPostId_whenPostExists_whenUpdatePost_thenReturnPostDto() {
        PostRequest postRequest = PostRequestHelper.generate.get();

        Post post = Post.builder()
                .id(UUID.randomUUID())
                .author(postRequest.getAuthor())
                .content(postRequest.getContent())
                .viewCount(postRequest.getViewCount())
                .postDate(postRequest.getPostDate().atZone(ZoneId.systemDefault()))
                .build();

        PostDto expectedPostDto = PostDto.builder()
                .id(post.getId())
                .author(post.getAuthor())
                .content(post.getContent())
                .viewCount(post.getViewCount())
                .postDate(post.getPostDate().toLocalDateTime())
                .build();

        when(this.postRepository.save(any(Post.class))).thenReturn(post);

        when(this.postRepository.findById(post.getId())).thenReturn(Optional.of(post));

        doNothing().when(this.messageProducer).sendForUpsert(post);

        PostDto actualPostDto = this.postService.updatePost(post.getId(), postRequest);

        PostDtoHelper.compare.accept(expectedPostDto, actualPostDto);

        verify(this.postRepository, times(1)).save(this.postCaptor.capture());

        PostHelper.compare.accept(post, this.postCaptor.getValue());

        verify(this.postRepository, times(1)).findById(this.postIdCaptor.capture());

        PostHelper.comparePostId.accept(post.getId(), this.postIdCaptor.getValue());

        verify(this.messageProducer, times(1)).sendForUpsert(this.postCaptor.capture());

        PostHelper.compare.accept(post, this.postCaptor.getValue());
    }

    @Test
    void givenPostRequestAndPostId_whenPostDoesNotExist_whenUpdatePost_thenThrowPostNotFoundException() {
        PostRequest postRequest = PostRequestHelper.generate.get();

        UUID postId = UUID.randomUUID();

        when(this.postRepository.findById(postId)).thenReturn(Optional.empty());

        try {
            this.postService.updatePost(postId, postRequest);

        } catch (PostNotFoundException ex) {
            verify(this.postRepository, times(0)).save(any(Post.class));

            verify(this.messageProducer, times(0)).sendForUpsert(any(Post.class));

            verify(this.postRepository, times(1)).findById(this.postIdCaptor.capture());

            PostHelper.comparePostId.accept(postId, this.postIdCaptor.getValue());
        }
    }
}
