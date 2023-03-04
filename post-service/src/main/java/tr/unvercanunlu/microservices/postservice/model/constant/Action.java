package tr.unvercanunlu.microservices.postservice.model.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@ToString
@Getter
@RequiredArgsConstructor
public enum Action implements Serializable {

    DELETE("delete", "Delete Post"),

    UPSERT("upsert", "Update or Insert Post");

    private final String code;

    private final String description;

    @JsonValue
    public String getCode() {
        return code;
    }
}