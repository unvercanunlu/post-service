package tr.unvercanunlu.microservices.postservice.controller.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tr.unvercanunlu.microservices.postservice.exception.PostNotFoundException;
import tr.unvercanunlu.microservices.postservice.model.entity.PostHelper;
import tr.unvercanunlu.microservices.postservice.model.request.PostRequest;
import tr.unvercanunlu.microservices.postservice.model.request.PostRequestHelper;
import tr.unvercanunlu.microservices.postservice.model.response.PostDto;
import tr.unvercanunlu.microservices.postservice.model.response.PostDtoHelper;
import tr.unvercanunlu.microservices.postservice.service.IPostService;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.*;
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

    @MockBean
    private Logger logger;

    private final ArgumentCaptor<UUID> idCaptor = ArgumentCaptor.forClass(UUID.class);

    private final ArgumentCaptor<PostRequest> postRequestCaptor = ArgumentCaptor.forClass(PostRequest.class);

    private final BiConsumer<String, Map<String, Object>> comparePostNotFoundError = (postId, error) -> {
        assertNotNull(error);

        assertTrue(error.containsKey("reason"));
        assertNotNull(error.get("reason"));
        assertEquals("Post not found with " + postId + " ID", error.get("reason"));

        assertTrue(error.containsKey("data"));
        assertNotNull(error.get("data"));

        Map<String, String> data = (Map<String, String>) error.get("data");

        assertTrue(data.containsKey("postId"));
        assertNotNull(data.get("postId"));
        assertEquals(postId, data.get("postId"));
    };

    @Test
    void givenPostRequest_whenCreatePost_thenReturnPostDto() throws Exception {
        doNothing().when(this.logger).info(any(String.class));

        PostRequest postRequest = PostRequestHelper.generate.get();

        PostDto expectedPostDto = PostDtoHelper.generate.get();

        when(this.postService.createPost(any(PostRequest.class))).thenReturn(expectedPostDto);

        MvcResult result = this.mockMvc.perform(post("/api/v1/posts")
                        .content(this.objectMapper.writeValueAsString(postRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(HttpStatus.CREATED.value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        PostDto actualPostDto = this.objectMapper.readValue(result.getResponse().getContentAsString(), PostDto.class);

        PostDtoHelper.compare.accept(expectedPostDto, actualPostDto);

        verify(this.postService, times(1)).createPost(this.postRequestCaptor.capture());

        PostRequestHelper.compare.accept(postRequest, this.postRequestCaptor.getValue());
    }

    @Test
    void givenPostId_whenPostExists_whenDeletePost_thenReturnSuccess() throws Exception {
        doNothing().when(this.logger).info(any(String.class));

        UUID postId = UUID.randomUUID();

        doNothing().when(this.postService).deletePost(postId);

        this.mockMvc.perform(delete("/api/v1/posts/" + postId))
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn();

        verify(this.postService, times(1)).deletePost(this.idCaptor.capture());

        PostHelper.comparePostId.accept(postId, this.idCaptor.getValue());
    }

    @Test
    void givenPostId_whenPostDoesNotExist_whenDeletePost_thenReturnFail() throws Exception {
        doNothing().when(this.logger).info(any(String.class));

        UUID postId = UUID.randomUUID();

        doThrow(new PostNotFoundException(postId)).when(this.postService).deletePost(postId);

        MvcResult result = this.mockMvc.perform(delete("/api/v1/posts/" + postId))
                .andDo(print())
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        Map<String, Object> error = this.objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<Map<String, Object>>() {
        });

        this.comparePostNotFoundError.accept(postId.toString(), error);

        verify(this.postService, times(1)).deletePost(this.idCaptor.capture());

        PostHelper.comparePostId.accept(postId, this.idCaptor.getValue());
    }

    @Test
    void givenPostRequest_whenPostExists_whenGetPost_thenReturnPostDto() throws Exception {
        doNothing().when(this.logger).info(any(String.class));

        PostDto expectedPostDto = PostDtoHelper.generate.get();

        UUID postId = expectedPostDto.getId();

        when(this.postService.getPost(any(UUID.class))).thenReturn(expectedPostDto);

        MvcResult result = this.mockMvc.perform(get("/api/v1/posts/" + postId.toString()))
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        PostDto actualPostDto = this.objectMapper.readValue(result.getResponse().getContentAsString(), PostDto.class);

        PostDtoHelper.compare.accept(expectedPostDto, actualPostDto);

        verify(this.postService, times(1)).getPost(this.idCaptor.capture());

        PostHelper.comparePostId.accept(postId, this.idCaptor.getValue());
    }

    @Test
    void givenPostId_whenPostDoesNotExist_whenGetPost_thenReturnFail() throws Exception {
        doNothing().when(this.logger).info(any(String.class));

        UUID postId = UUID.randomUUID();

        doThrow(new PostNotFoundException(postId)).when(this.postService).getPost(postId);

        MvcResult result = this.mockMvc.perform(get("/api/v1/posts/" + postId))
                .andDo(print())
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        Map<String, Object> error = this.objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<Map<String, Object>>() {
        });

        this.comparePostNotFoundError.accept(postId.toString(), error);

        verify(this.postService, times(1)).getPost(this.idCaptor.capture());

        PostHelper.comparePostId.accept(postId, this.idCaptor.getValue());
    }

    @Test
    void whenGetAllPosts_thenReturnListOfPostDtos() throws Exception {
        doNothing().when(this.logger).info(any(String.class));

        PostDto expectedPostDto = PostDtoHelper.generate.get();

        List<PostDto> expectedPostDtos = List.of(expectedPostDto);

        when(this.postService.getAllPosts()).thenReturn(expectedPostDtos);

        MvcResult result = this.mockMvc.perform(get("/api/v1/posts"))
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        PostDto[] actualPostDtos = this.objectMapper.readValue(result.getResponse().getContentAsString(), PostDto[].class);

        assertNotNull(actualPostDtos);
        assertEquals(expectedPostDtos.size(), actualPostDtos.length);

        PostDto actualPostDto = actualPostDtos[0];

        PostDtoHelper.compare.accept(expectedPostDto, actualPostDto);

        verify(this.postService, times(1)).getAllPosts();
    }

    @Test
    void givenPostRequestAndPostId_whenPostExists_whenUpdatePost_thenReturnPostDto() throws Exception {
        doNothing().when(this.logger).info(any(String.class));

        PostRequest postRequest = PostRequestHelper.generate.get();

        UUID postId = UUID.randomUUID();

        PostDto expectedPostDto = PostDtoHelper.generate.get();

        when(this.postService.updatePost(any(UUID.class), any(PostRequest.class))).thenReturn(expectedPostDto);

        MvcResult result = this.mockMvc.perform(put("/api/v1/posts/" + postId)
                        .content(this.objectMapper.writeValueAsString(postRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        PostDto actualPostDto = this.objectMapper.readValue(result.getResponse().getContentAsString(), PostDto.class);

        PostDtoHelper.compare.accept(expectedPostDto, actualPostDto);

        verify(this.postService, times(1)).updatePost(this.idCaptor.capture(), this.postRequestCaptor.capture());

        PostHelper.comparePostId.accept(postId, this.idCaptor.getValue());

        PostRequestHelper.compare.accept(postRequest, this.postRequestCaptor.getValue());
    }

    @Test
    void givenPostRequestAndPostId_whenPostDoesNotExist_whenUpdatePost_thenReturnFail() throws Exception {
        doNothing().when(this.logger).info(any(String.class));

        PostRequest postRequest = PostRequestHelper.generate.get();

        UUID postId = UUID.randomUUID();

        doThrow(new PostNotFoundException(postId)).when(this.postService).updatePost(any(UUID.class), any(PostRequest.class));

        MvcResult result = this.mockMvc.perform(put("/api/v1/posts/" + postId)
                        .content(this.objectMapper.writeValueAsString(postRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        Map<String, Object> error = this.objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<Map<String, Object>>() {
        });

        this.comparePostNotFoundError.accept(postId.toString(), error);

        verify(this.postService, times(1)).updatePost(this.idCaptor.capture(), this.postRequestCaptor.capture());

        PostHelper.comparePostId.accept(postId, this.idCaptor.getValue());

        PostRequestHelper.compare.accept(postRequest, this.postRequestCaptor.getValue());
    }
}
