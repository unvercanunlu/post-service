package tr.unvercanunlu.microservices.postservice.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Post extends BaseEntity implements Serializable {

    @Column(name = "author")
    private String author;

    @Column(name = "content")
    private String content;

    @Column(name = "viewCount")
    private Long viewCount;

    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "post_date")
    private ZonedDateTime postDate;
}
