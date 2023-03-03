package tr.unvercanunlu.postservice.controller.impl;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tr.unvercanunlu.postservice.config.ApiConfig;
import tr.unvercanunlu.postservice.controller.IPostController;
import tr.unvercanunlu.postservice.model.request.PostRequest;
import tr.unvercanunlu.postservice.model.response.PostDto;
import tr.unvercanunlu.postservice.service.IPostService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = ApiConfig.POST_API)
public class PostController implements IPostController {

    private final IPostService postService;

    public PostController(IPostService postService) {
        this.postService = postService;
    }

    @Override
    @GetMapping
    public ResponseEntity<List<PostDto>> getAllPosts() {
        List<PostDto> postDtos = this.postService.getAllPosts();
        return ResponseEntity.status(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON)
                .body(postDtos);
    }

    @Override
    @GetMapping(path = "/{postId}")
    public ResponseEntity<PostDto> getPost(@PathVariable(name = "postId") UUID postId) {
        PostDto postDto = this.postService.getPost(postId);
        return ResponseEntity.status(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON)
                .body(postDto);
    }

    @Override
    @DeleteMapping(path = "/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable(name = "postId") UUID postId) {
        this.postService.deletePost(postId);
        return ResponseEntity.status(HttpStatus.OK.value()).build();
    }

    @Override
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostDto> createPost(@RequestBody PostRequest postRequest) {
        PostDto postDto = this.postService.createPost(postRequest);
        return ResponseEntity.status(HttpStatus.CREATED.value())
                .contentType(MediaType.APPLICATION_JSON)
                .body(postDto);
    }

    @Override
    @PutMapping(path = "/{postId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostDto> updatePost(@PathVariable(name = "postId") UUID postId, @RequestBody PostRequest postRequest) {
        PostDto postDto = this.postService.updatePost(postId, postRequest);
        return ResponseEntity.status(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON)
                .body(postDto);
    }

}
