package tr.unvercanunlu.microservices.postservice.controller.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tr.unvercanunlu.microservices.postservice.config.ApiConfig;
import tr.unvercanunlu.microservices.postservice.controller.IPostController;
import tr.unvercanunlu.microservices.postservice.model.constant.Order;
import tr.unvercanunlu.microservices.postservice.model.request.PostRequest;
import tr.unvercanunlu.microservices.postservice.model.response.PostDto;
import tr.unvercanunlu.microservices.postservice.service.IPostService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = ApiConfig.POST_API)
public class PostController implements IPostController {

    private final Logger logger = LoggerFactory.getLogger(PostController.class);

    private final IPostService postService;

    public PostController(IPostService postService) {
        this.postService = postService;
    }

    @Override
    @GetMapping
    public ResponseEntity<List<PostDto>> getTopOrderedPosts(
            @RequestParam(name = "order", defaultValue = ApiConfig.DEFAULT_ORDER) String orderName,
            @RequestParam(name = "top", defaultValue = ApiConfig.DEFAULT_TOP) Integer top
    ) {
        this.logger.info("Get request for ordered by " + orderName + " and top " + top + " is received.");

        Order order = Order.getByName.apply(orderName);

        List<PostDto> postDtos = this.postService.getTopOrderedPosts(order, top);

        return ResponseEntity.status(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON)
                .body(postDtos);
    }

    @Override
    @GetMapping(path = "/{postId}")
    public ResponseEntity<PostDto> getPost(@PathVariable(name = "postId") UUID postId) {
        this.logger.info("Get request with " + postId + " ID is received.");

        PostDto postDto = this.postService.getPost(postId);

        return ResponseEntity.status(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON)
                .body(postDto);
    }

    @Override
    @DeleteMapping(path = "/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable(name = "postId") UUID postId) {
        this.logger.info("Delete request with " + postId + " ID is received.");

        this.postService.deletePost(postId);

        return ResponseEntity.status(HttpStatus.OK.value()).build();
    }

    @Override
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostDto> createPost(@RequestBody PostRequest postRequest) {
        this.logger.info("Post request with " + postRequest + " is received.");

        PostDto postDto = this.postService.createPost(postRequest);

        return ResponseEntity.status(HttpStatus.CREATED.value())
                .contentType(MediaType.APPLICATION_JSON)
                .body(postDto);
    }

    @Override
    @PutMapping(path = "/{postId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostDto> updatePost(
            @PathVariable(name = "postId") UUID postId,
            @RequestBody PostRequest postRequest
    ) {
        this.logger.info("Put request with " + postRequest + " and " + postId + " ID is received.");

        PostDto postDto = this.postService.updatePost(postId, postRequest);

        return ResponseEntity.status(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON)
                .body(postDto);
    }

    @Override
    @RequestMapping(path = "/{postId}", method = RequestMethod.HEAD)
    public ResponseEntity<Void> checkExistsPost(@PathVariable(name = "postId") UUID postId) {
        this.logger.info("Head request with " + postId + " ID is received.");

        this.postService.checkExistsPost(postId);

        return ResponseEntity.status(HttpStatus.OK.value()).build();
    }

    /*

    @Override
    @PatchMapping(path = "/{postId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostDto> updatePartialPost(
            @PathVariable(name = "postId") UUID postId,
            @RequestBody PostRequest postRequest
    ) {
        this.logger.info("Put request with " + postRequest + " and " + postId + " ID is received.");

        PostDto postDto = this.postService.updatePartialPost(postId, postRequest);

        return ResponseEntity.status(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON)
                .body(postDto);
    }

    */
}
