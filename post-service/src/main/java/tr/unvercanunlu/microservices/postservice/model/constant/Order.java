package tr.unvercanunlu.microservices.postservice.model.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import tr.unvercanunlu.microservices.postservice.exception.OrderNotSuitableException;

import java.io.Serializable;
import java.util.Arrays;
import java.util.function.Function;

@ToString
@Getter
@RequiredArgsConstructor
public enum Order implements Serializable {

    VIEW("view", "Order By Post View Count"),

    DATE("date", "Order By Post Date");

    public static final Function<String, Order> getByName = (name) ->
            Arrays.stream(Order.values())
                    .filter(o -> o.getCode().equals(name))
                    .findAny()
                    .orElseThrow(() -> new OrderNotSuitableException(name));
    private final String code;
    private final String description;

    @JsonValue
    public String getCode() {
        return code;
    }

    public static class Names {

        public static final String VIEW = "view";

        public static final String DATE_NAME = "date";

        private Names() {
        }
    }
}
