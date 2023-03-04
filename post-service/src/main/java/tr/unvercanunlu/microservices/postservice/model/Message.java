package tr.unvercanunlu.microservices.postservice.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tr.unvercanunlu.microservices.postservice.model.constant.Action;

@Getter
@Setter
@Builder
@ToString
public class Message {

    private Action action;

    private String data;
}
