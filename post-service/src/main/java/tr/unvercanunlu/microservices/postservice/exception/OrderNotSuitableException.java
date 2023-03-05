package tr.unvercanunlu.microservices.postservice.exception;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class OrderNotSuitableException extends RuntimeException implements Serializable {

    private final String order;

    public OrderNotSuitableException(String order) {
        super("Order with " + order + " ID is not suitable.");
        this.order = order;
    }
}
