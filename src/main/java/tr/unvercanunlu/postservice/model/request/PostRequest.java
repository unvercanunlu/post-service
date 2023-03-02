package tr.unvercanunlu.postservice.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import tr.unvercanunlu.postservice.config.DateConfig;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Getter
@Setter
@Builder
public class PostRequest implements Serializable {

    private String author;

    private String content;

    private Integer viewCount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateConfig.ZONED_DATE_TIME_FORMAT)
    private ZonedDateTime postDate;

}
