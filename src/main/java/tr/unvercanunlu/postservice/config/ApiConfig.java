package tr.unvercanunlu.postservice.config;

public class ApiConfig {

    public static final String VERSION = "v1";

    public static final String BASE = "/" + "api" + "/" + VERSION;

    public static final String POST_API = BASE + "/" + "posts";

    private ApiConfig() {
    }
}
