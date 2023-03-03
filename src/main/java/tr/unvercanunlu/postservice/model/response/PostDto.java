package tr.unvercanunlu.postservice.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tr.unvercanunlu.postservice.config.DateConfig;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
public class PostDto implements Serializable {

    private UUID id;

    private String author;

    private String content;

    private Long viewCount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateConfig.DATE_TIME_FORMAT)
    private LocalDateTime postDate;
}
