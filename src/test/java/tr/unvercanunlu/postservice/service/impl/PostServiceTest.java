package tr.unvercanunlu.postservice.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tr.unvercanunlu.postservice.exception.PostNotFoundException;
import tr.unvercanunlu.postservice.model.entity.Post;
import tr.unvercanunlu.postservice.model.request.PostRequest;
import tr.unvercanunlu.postservice.model.response.PostDto;
import tr.unvercanunlu.postservice.repository.IPostRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(value = MockitoExtension.class)
class PostServiceTest {

    @Mock
    private IPostRepository postRepository;

    @InjectMocks
    private PostService postService;

    @Test
    void whenGetAllPosts_thenReturnListOfPosts() {
        Post post = Post.builder()
                .id(UUID.randomUUID())
                .author("author-1")
                .content("content-1")
                .viewCount(1L)
                .postDate(ZonedDateTime.now())
                .build();

        List<Post> posts = List.of(post);

        PostDto postDto = PostDto.builder()
                .id(post.getId())
                .author(post.getAuthor())
                .content(post.getContent())
                .viewCount(post.getViewCount())
                .postDate(post.getPostDate().toLocalDateTime())
                .build();

        List<PostDto> expectedPostDtos = List.of(postDto);

        when(this.postRepository.findAll()).thenReturn(posts);

        List<PostDto> actualPostDtos = this.postService.getAllPosts();

        verify(this.postRepository, times(1)).findAll();

        assertNotNull(actualPostDtos);
        assertEquals(expectedPostDtos.size(), actualPostDtos.size());

        assertNotNull(actualPostDtos.get(0));

        assertNotNull(actualPostDtos.get(0).getId());
        assertEquals(postDto.getId(), actualPostDtos.get(0).getId());

        assertNotNull(actualPostDtos.get(0).getContent());
        assertEquals(postDto.getContent(), actualPostDtos.get(0).getContent());

        assertNotNull(actualPostDtos.get(0).getAuthor());
        assertEquals(postDto.getAuthor(), actualPostDtos.get(0).getAuthor());

        assertNotNull(actualPostDtos.get(0).getPostDate());
        assertEquals(postDto.getPostDate(), actualPostDtos.get(0).getPostDate());

        assertNotNull(actualPostDtos.get(0).getViewCount());
        assertEquals(postDto.getViewCount(), actualPostDtos.get(0).getViewCount());
    }

    @Test
    void givenPostId_whenPostExists_whenGetPost_thenReturnPostDto() {
        Post post = Post.builder()
                .id(UUID.randomUUID())
                .author("author-1")
                .content("content-1")
                .viewCount(1L)
                .postDate(ZonedDateTime.now())
                .build();

        PostDto expectedPostDto = PostDto.builder()
                .id(post.getId())
                .author(post.getAuthor())
                .content(post.getContent())
                .viewCount(post.getViewCount())
                .postDate(post.getPostDate().toLocalDateTime())
                .build();

        when(this.postRepository.findById(post.getId())).thenReturn(Optional.of(post));

        PostDto actualPostDto = this.postService.getPost(post.getId());

        ArgumentCaptor<UUID> idCaptor = ArgumentCaptor.forClass(UUID.class);

        verify(this.postRepository, times(1)).findById(idCaptor.capture());

        assertNotNull(idCaptor.getValue());
        assertEquals(post.getId(), idCaptor.getValue());

        assertNotNull(actualPostDto);

        assertNotNull(actualPostDto.getId());
        assertEquals(expectedPostDto.getId(), actualPostDto.getId());

        assertNotNull(actualPostDto.getContent());
        assertEquals(expectedPostDto.getContent(), actualPostDto.getContent());

        assertNotNull(actualPostDto.getAuthor());
        assertEquals(expectedPostDto.getAuthor(), actualPostDto.getAuthor());

        assertNotNull(actualPostDto.getPostDate());
        assertEquals(expectedPostDto.getPostDate(), actualPostDto.getPostDate());

        assertNotNull(actualPostDto.getViewCount());
        assertEquals(expectedPostDto.getViewCount(), actualPostDto.getViewCount());
    }

    @Test
    void givenPostId_whenPostDoesNotExist_whenGetPost_thenThrowPostNotFoundException() {
        UUID postId = UUID.randomUUID();

        when(this.postRepository.findById(postId)).thenReturn(Optional.empty());

        try {
            this.postService.getPost(postId);
        } catch (PostNotFoundException e) {
            ArgumentCaptor<UUID> idCaptor = ArgumentCaptor.forClass(UUID.class);

            verify(this.postRepository, times(1)).findById(idCaptor.capture());

            assertNotNull(idCaptor.getValue());
            assertEquals(idCaptor.getValue(), postId);
        }
    }

    @Test
    void givenPostId_whenDeletePost() {
        Post post = Post.builder()
                .id(UUID.randomUUID())
                .author("author-1")
                .content("content-1")
                .viewCount(1L)
                .postDate(ZonedDateTime.now())
                .build();

        when(this.postRepository.checkExistsById(post.getId())).thenReturn(true);

        this.postService.deletePost(post.getId());

        ArgumentCaptor<UUID> idCaptor = ArgumentCaptor.forClass(UUID.class);

        verify(this.postRepository, times(1)).checkExistsById(idCaptor.capture());

        assertNotNull(idCaptor.getValue());
        assertEquals(post.getId(), idCaptor.getValue());
    }

    @Test
    void givenPostId_whenPostDoesNotExist_whenDeletePost_thenThrowPostNotFoundException() {
        UUID postId = UUID.randomUUID();

        when(this.postRepository.checkExistsById(postId)).thenReturn(false);

        try {
            this.postService.deletePost(postId);
        } catch (PostNotFoundException e) {
            verify(this.postRepository, times(0)).deleteById(any(UUID.class));

            ArgumentCaptor<UUID> idCaptor = ArgumentCaptor.forClass(UUID.class);

            verify(this.postRepository, times(1)).checkExistsById(idCaptor.capture());

            assertNotNull(idCaptor.getValue());
            assertEquals(idCaptor.getValue(), postId);
        }
    }

    @Test
    void givenPostRequest_whenCreatePost_thenReturnPostDto() {
        PostRequest postRequest = PostRequest.builder()
                .author("author-1")
                .content("content-1")
                .viewCount(1L)
                .postDate(LocalDateTime.now())
                .build();

        Post post = Post.builder()
                .author(postRequest.getAuthor())
                .content(postRequest.getContent())
                .viewCount(postRequest.getViewCount())
                .postDate(postRequest.getPostDate().atZone(ZoneId.systemDefault()))
                .build();

        UUID postId = UUID.randomUUID();

        PostDto expectedPostDto = PostDto.builder()
                .id(postId)
                .author(post.getAuthor())
                .content(post.getContent())
                .viewCount(post.getViewCount())
                .postDate(post.getPostDate().toLocalDateTime())
                .build();

        when(this.postRepository.save(any(Post.class))).thenReturn(post);

        PostDto actualPostDto = this.postService.createPost(postRequest);

        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);

        verify(this.postRepository, times(1)).save(postCaptor.capture());

        assertNotNull(postCaptor.getValue());

        assertNull(postCaptor.getValue().getId());
        assertEquals(post.getId(), postCaptor.getValue().getId());

        assertNotNull(postCaptor.getValue().getContent());
        assertEquals(expectedPostDto.getContent(), actualPostDto.getContent());

        assertNotNull(postCaptor.getValue().getAuthor());
        assertEquals(expectedPostDto.getAuthor(), actualPostDto.getAuthor());

        assertNotNull(actualPostDto.getPostDate());
        assertEquals(expectedPostDto.getPostDate(), actualPostDto.getPostDate());

        assertNotNull(actualPostDto.getViewCount());
        assertEquals(expectedPostDto.getViewCount(), actualPostDto.getViewCount());
    }

    @Test
    void givenPostRequestAndPostId_whenPostExists_whenUpdatePost_thenReturnPostDto() {
        PostRequest postRequest = PostRequest.builder()
                .author("author-1")
                .content("content-1")
                .viewCount(1L)
                .postDate(LocalDateTime.now())
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

        when(this.postRepository.findById(post.getId())).thenReturn(Optional.of(post));

        PostDto actualPostDto = this.postService.updatePost(post.getId(), postRequest);

        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);

        verify(this.postRepository, times(1)).save(postCaptor.capture());

        ArgumentCaptor<UUID> idCaptor = ArgumentCaptor.forClass(UUID.class);

        verify(this.postRepository, times(1)).findById(idCaptor.capture());

        assertNotNull(idCaptor.getValue());
        assertEquals(post.getId(), idCaptor.getValue());

        assertNotNull(postCaptor.getValue());

        assertNotNull(postCaptor.getValue().getId());
        assertEquals(post.getId(), postCaptor.getValue().getId());

        assertNotNull(postCaptor.getValue().getContent());
        assertEquals(expectedPostDto.getContent(), actualPostDto.getContent());

        assertNotNull(postCaptor.getValue().getAuthor());
        assertEquals(expectedPostDto.getAuthor(), actualPostDto.getAuthor());

        assertNotNull(actualPostDto.getPostDate());
        assertEquals(expectedPostDto.getPostDate(), actualPostDto.getPostDate());

        assertNotNull(actualPostDto.getViewCount());
        assertEquals(expectedPostDto.getViewCount(), actualPostDto.getViewCount());
    }

    @Test
    void givenPostRequestAndPostId_whenPostDoesNotExist_whenUpdatePost_thenThrowPostNotFoundException() {
        PostRequest postRequest = PostRequest.builder()
                .author("author-1")
                .content("content-1")
                .viewCount(1L)
                .postDate(LocalDateTime.now())
                .build();

        UUID postId = UUID.randomUUID();

        when(this.postRepository.findById(postId)).thenReturn(Optional.empty());

        try {
            this.postService.updatePost(postId, postRequest);
        } catch (PostNotFoundException e) {
            verify(this.postRepository, times(0)).save(any(Post.class));

            ArgumentCaptor<UUID> idCaptor = ArgumentCaptor.forClass(UUID.class);

            verify(this.postRepository, times(1)).findById(idCaptor.capture());

            assertNotNull(idCaptor.getValue());
            assertEquals(idCaptor.getValue(), postId);
        }
    }
}
