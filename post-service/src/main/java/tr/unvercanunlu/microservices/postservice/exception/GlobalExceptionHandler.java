package tr.unvercanunlu.microservices.postservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = PostNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlePostNotFoundException(PostNotFoundException exception) {
        Map<String, Object> errorMap = new HashMap<>();
        errorMap.put("reason", exception.getMessage());

        Optional.ofNullable(exception.getPostId()).ifPresent(postId -> {
            Map<String, String> dataMap = new HashMap<>();
            dataMap.put("postId", postId.toString());
            errorMap.put("data", dataMap);
        });

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorMap);
    }

    @ExceptionHandler(value = OrderNotSuitableException.class)
    public ResponseEntity<Map<String, Object>> handleOrderNotSuitableException(OrderNotSuitableException exception) {
        Map<String, Object> errorMap = new HashMap<>();
        errorMap.put("reason", exception.getMessage());

        Optional.ofNullable(exception.getOrder()).ifPresent(order -> {
            Map<String, String> dataMap = new HashMap<>();
            dataMap.put("order", order);
            errorMap.put("data", dataMap);
        });

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorMap);
    }
}
