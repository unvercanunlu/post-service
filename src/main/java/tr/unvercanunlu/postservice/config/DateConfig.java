package tr.unvercanunlu.postservice.config;

public class DateConfig {

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static final String TIME_FORMAT = "HH:mm:ss";

    public static final String DATE_TIME_FORMAT = DATE_FORMAT + "'T'" + TIME_FORMAT;

    public static final String ZONED_DATE_TIME_FORMAT = DATE_TIME_FORMAT + "Z";

}
