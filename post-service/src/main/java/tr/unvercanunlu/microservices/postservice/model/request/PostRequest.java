package tr.unvercanunlu.microservices.postservice.model.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;
import tr.unvercanunlu.microservices.postservice.config.DateConfig;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
public class PostRequest implements Serializable {

    private String author;

    private String content;

    private Long viewCount;

    @DateTimeFormat(pattern = DateConfig.DATE_TIME_FORMAT)
    private LocalDateTime postDate;
}
