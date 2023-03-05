package tr.unvercanunlu.microservices.postservice.model.constant;

import java.util.Optional;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OrderHelper {

    public static final BiConsumer<Integer, Integer> compareTop = (expected, actual) ->
            Optional.ofNullable(expected).ifPresent(t -> {
                assertNotNull(actual);
                assertEquals(expected, actual);
            });

    public static final BiConsumer<Order, Order> compareOrder = (expected, actual) ->
            Optional.ofNullable(expected).ifPresent(t -> {
                assertNotNull(actual);
                assertEquals(expected, actual);
            });
}
