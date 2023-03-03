package tr.unvercanunlu.postservice.model.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import tr.unvercanunlu.postservice.config.DateConfig;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class PostRequest implements Serializable {

    private String author;

    private String content;

    private Long viewCount;

    @DateTimeFormat(pattern = DateConfig.DATE_TIME_FORMAT)
    private LocalDateTime postDate;

}
