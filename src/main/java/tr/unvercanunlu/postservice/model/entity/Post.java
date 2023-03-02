package tr.unvercanunlu.postservice.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Post extends BaseEntity implements Serializable {

    @Column(name = "author")
    private String author;

    @Column(name = "content")
    private String content;

    @Column(name = "viewCount")
    private Integer viewCount;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "post_date")
    private ZonedDateTime postDate;

}
