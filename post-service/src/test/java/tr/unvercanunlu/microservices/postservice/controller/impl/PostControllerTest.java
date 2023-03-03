package tr.unvercanunlu.microservices.postservice.controller.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tr.unvercanunlu.microservices.postservice.config.DateConfig;
import tr.unvercanunlu.microservices.postservice.exception.PostNotFoundException;
import tr.unvercanunlu.microservices.postservice.model.entity.Post;
import tr.unvercanunlu.microservices.postservice.model.request.PostRequest;
import tr.unvercanunlu.microservices.postservice.model.response.PostDto;
import tr.unvercanunlu.microservices.postservice.service.IPostService;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IPostService postService;

    @Test
    void givenPostRequest_whenCreatePost_thenReturnPostDto() throws Exception {
        PostRequest postRequest = PostRequest.builder()
                .author("author-1")
                .content("content-1")
                .viewCount(1L)
                .postDate(LocalDateTime.now())
                .build();

        PostDto expectedPostDto = PostDto.builder()
                .id(UUID.randomUUID())
                .author(postRequest.getAuthor())
                .content(postRequest.getContent())
                .viewCount(postRequest.getViewCount())
                .postDate(LocalDateTime.parse(postRequest.getPostDate().format(DateConfig.DATE_TIME_FORMATTER), DateConfig.DATE_TIME_FORMATTER))
                .build();

        when(this.postService.createPost(any(PostRequest.class))).thenReturn(expectedPostDto);

        this.mockMvc.perform(post("/api/v1/posts")
                        .content(this.objectMapper.writeValueAsString(postRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(HttpStatus.CREATED.value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(this.objectMapper.writeValueAsString(expectedPostDto)))
                .andReturn();

        ArgumentCaptor<PostRequest> requestCaptor = ArgumentCaptor.forClass(PostRequest.class);

        verify(this.postService, times(1)).createPost(requestCaptor.capture());

        assertNotNull(requestCaptor.getValue());

        assertNotNull(requestCaptor.getValue().getPostDate());
        assertEquals(postRequest.getPostDate(), requestCaptor.getValue().getPostDate());

        assertNotNull(requestCaptor.getValue().getAuthor());
        assertEquals(postRequest.getAuthor(), requestCaptor.getValue().getAuthor());

        assertNotNull(requestCaptor.getValue().getContent());
        assertEquals(postRequest.getContent(), requestCaptor.getValue().getContent());

        assertNotNull(requestCaptor.getValue().getViewCount());
        assertEquals(postRequest.getViewCount(), requestCaptor.getValue().getViewCount());
    }

    @Test
    void givenPostId_whenPostExists_whenDeletePost_thenReturnSuccess() throws Exception {
        UUID postId = UUID.randomUUID();

        doNothing().when(this.postService).deletePost(postId);

        this.mockMvc.perform(delete("/api/v1/posts/" + postId))
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();

        ArgumentCaptor<UUID> requestCaptor = ArgumentCaptor.forClass(UUID.class);

        verify(this.postService, times(1)).deletePost(requestCaptor.capture());

        assertNotNull(requestCaptor.getValue());

        assertEquals(postId, requestCaptor.getValue());
    }

    @Test
    void givenPostId_whenPostDoesNotExist_whenDeletePost_thenReturnFail() throws Exception {
        UUID postId = UUID.randomUUID();

        doThrow(new PostNotFoundException(postId)).when(this.postService).deletePost(postId);

        MvcResult result = this.mockMvc.perform(delete("/api/v1/posts/" + postId))
                .andDo(print())
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        Map<String, Object> errorResponse = this.objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);

        ArgumentCaptor<UUID> requestCaptor = ArgumentCaptor.forClass(UUID.class);

        verify(this.postService, times(1)).deletePost(requestCaptor.capture());

        assertNotNull(errorResponse);

        assertNotNull(errorResponse.get("reason"));
        assertEquals("Post not found with " + postId + " ID", errorResponse.get("reason"));

        assertNotNull(errorResponse.get("data"));
        Map<String, String> errorData = (Map<String, String>) errorResponse.get("data");
        assertNotNull(errorData.get("postId"));
        assertEquals(postId.toString(), errorData.get("postId"));

        assertNotNull(requestCaptor.getValue());

        assertEquals(postId, requestCaptor.getValue());
    }

    @Test
    void givenPostRequest_whenPostExists_whenGetPost_thenReturnPostDto() throws Exception {
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
                .postDate(LocalDateTime.parse(post.getPostDate().format(DateConfig.DATE_TIME_FORMATTER), DateConfig.DATE_TIME_FORMATTER))
                .build();

        when(this.postService.getPost(post.getId())).thenReturn(expectedPostDto);

        this.mockMvc.perform(get("/api/v1/posts/" + post.getId()))
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(this.objectMapper.writeValueAsString(expectedPostDto)))
                .andReturn();

        ArgumentCaptor<UUID> idCaptor = ArgumentCaptor.forClass(UUID.class);

        verify(this.postService, times(1)).getPost(idCaptor.capture());

        assertNotNull(idCaptor.getValue());
        assertEquals(post.getId(), idCaptor.getValue());
    }

    @Test
    void givenPostId_whenPostDoesNotExist_whenGetPost_thenReturnFail() throws Exception {
        UUID postId = UUID.randomUUID();

        doThrow(new PostNotFoundException(postId)).when(this.postService).getPost(postId);

        MvcResult result = this.mockMvc.perform(get("/api/v1/posts/" + postId))
                .andDo(print())
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        Map<String, Object> errorResponse = this.objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);

        ArgumentCaptor<UUID> requestCaptor = ArgumentCaptor.forClass(UUID.class);

        verify(this.postService, times(1)).getPost(requestCaptor.capture());

        assertNotNull(errorResponse);

        assertNotNull(errorResponse.get("reason"));
        assertEquals("Post not found with " + postId + " ID", errorResponse.get("reason"));

        assertNotNull(errorResponse.get("data"));
        Map<String, String> errorData = (Map<String, String>) errorResponse.get("data");
        assertNotNull(errorData.get("postId"));
        assertEquals(postId.toString(), errorData.get("postId"));

        assertNotNull(requestCaptor.getValue());

        assertEquals(postId, requestCaptor.getValue());
    }

    @Test
    void whenGetAllPosts_thenReturnListOfPostDtos() throws Exception {
        Post post = Post.builder()
                .id(UUID.randomUUID())
                .author("author-1")
                .content("content-1")
                .viewCount(1L)
                .postDate(ZonedDateTime.now())
                .build();

        PostDto postDto = PostDto.builder()
                .id(post.getId())
                .author(post.getAuthor())
                .content(post.getContent())
                .viewCount(post.getViewCount())
                .postDate(LocalDateTime.parse(post.getPostDate().format(DateConfig.DATE_TIME_FORMATTER), DateConfig.DATE_TIME_FORMATTER))
                .build();

        List<PostDto> expectedPostDtos = List.of(postDto);

        when(this.postService.getAllPosts()).thenReturn(expectedPostDtos);

        this.mockMvc.perform(get("/api/v1/posts"))
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(this.objectMapper.writeValueAsString(expectedPostDtos)))
                .andReturn();

        verify(this.postService, times(1)).getAllPosts();
    }

    @Test
    void givenPostRequestAndPostId_whenPostExists_whenUpdatePost_thenReturnPostDto() throws Exception {
        PostRequest postRequest = PostRequest.builder()
                .author("author-1")
                .content("content-1")
                .viewCount(1L)
                .postDate(LocalDateTime.now())
                .build();

        UUID postId = UUID.randomUUID();

        PostDto expectedPostDto = PostDto.builder()
                .id(postId)
                .author(postRequest.getAuthor())
                .content(postRequest.getContent())
                .viewCount(postRequest.getViewCount())
                .postDate(LocalDateTime.parse(postRequest.getPostDate().format(DateConfig.DATE_TIME_FORMATTER), DateConfig.DATE_TIME_FORMATTER))
                .build();

        when(this.postService.updatePost(any(UUID.class), any(PostRequest.class))).thenReturn(expectedPostDto);

        this.mockMvc.perform(put("/api/v1/posts/" + postId)
                        .content(this.objectMapper.writeValueAsString(postRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(this.objectMapper.writeValueAsString(expectedPostDto)))
                .andReturn();

        ArgumentCaptor<UUID> idCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<PostRequest> requestCaptor = ArgumentCaptor.forClass(PostRequest.class);

        verify(this.postService, times(1)).updatePost(idCaptor.capture(), requestCaptor.capture());

        assertNotNull(idCaptor.getValue());
        assertEquals(postId, idCaptor.getValue());

        assertNotNull(requestCaptor.getValue());

        assertNotNull(requestCaptor.getValue().getPostDate());
        assertEquals(postRequest.getPostDate(), requestCaptor.getValue().getPostDate());

        assertNotNull(requestCaptor.getValue().getAuthor());
        assertEquals(postRequest.getAuthor(), requestCaptor.getValue().getAuthor());

        assertNotNull(requestCaptor.getValue().getContent());
        assertEquals(postRequest.getContent(), requestCaptor.getValue().getContent());

        assertNotNull(requestCaptor.getValue().getViewCount());
        assertEquals(postRequest.getViewCount(), requestCaptor.getValue().getViewCount());
    }

    @Test
    void givenPostRequestAndPostId_whenPostDoesNotExist_whenUpdatePost_thenReturnFail() throws Exception {
        PostRequest postRequest = PostRequest.builder()
                .author("author-1")
                .content("content-1")
                .viewCount(1L)
                .postDate(LocalDateTime.now())
                .build();

        UUID postId = UUID.randomUUID();

        doThrow(new PostNotFoundException(postId)).when(this.postService).updatePost(any(UUID.class), any(PostRequest.class));

        MvcResult result = this.mockMvc.perform(put("/api/v1/posts/" + postId)
                        .content(this.objectMapper.writeValueAsString(postRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        Map<String, Object> errorResponse = this.objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);

        ArgumentCaptor<UUID> idCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<PostRequest> requestCaptor = ArgumentCaptor.forClass(PostRequest.class);

        verify(this.postService, times(1)).updatePost(idCaptor.capture(), requestCaptor.capture());

        assertNotNull(idCaptor.getValue());
        assertEquals(postId, idCaptor.getValue());

        assertNotNull(requestCaptor.getValue());

        assertNotNull(requestCaptor.getValue().getPostDate());
        assertEquals(postRequest.getPostDate(), requestCaptor.getValue().getPostDate());

        assertNotNull(requestCaptor.getValue().getAuthor());
        assertEquals(postRequest.getAuthor(), requestCaptor.getValue().getAuthor());

        assertNotNull(requestCaptor.getValue().getContent());
        assertEquals(postRequest.getContent(), requestCaptor.getValue().getContent());

        assertNotNull(requestCaptor.getValue().getViewCount());
        assertEquals(postRequest.getViewCount(), requestCaptor.getValue().getViewCount());

        assertNotNull(errorResponse);

        assertNotNull(errorResponse.get("reason"));
        assertEquals("Post not found with " + postId + " ID", errorResponse.get("reason"));

        assertNotNull(errorResponse.get("data"));
        Map<String, String> errorData = (Map<String, String>) errorResponse.get("data");
        assertNotNull(errorData.get("postId"));
        assertEquals(postId.toString(), errorData.get("postId"));
    }
}
