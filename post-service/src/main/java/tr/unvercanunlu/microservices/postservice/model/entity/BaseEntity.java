package tr.unvercanunlu.microservices.postservice.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@ToString
@SuperBuilder
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEntity implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    private UUID id;
}
