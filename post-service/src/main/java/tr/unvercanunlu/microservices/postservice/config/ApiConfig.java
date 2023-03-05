package tr.unvercanunlu.microservices.postservice.config;

public class ApiConfig {

    public static final String VERSION = "v1";

    public static final String BASE = "/" + "api" + "/" + VERSION;

    public static final String POST_API = BASE + "/" + "posts";

    public static final String DEFAULT_ORDER = "view";

    public static final String DEFAULT_TOP = "10";

    private ApiConfig() {
    }
}
