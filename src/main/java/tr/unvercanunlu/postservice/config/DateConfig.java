package tr.unvercanunlu.postservice.config;

import java.time.format.DateTimeFormatter;

public class DateConfig {

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static final String TIME_FORMAT = "HH:mm:ss";

    public static final String DATE_TIME_FORMAT = DATE_FORMAT + "'T'" + TIME_FORMAT;

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);

    private DateConfig() {
    }
}
