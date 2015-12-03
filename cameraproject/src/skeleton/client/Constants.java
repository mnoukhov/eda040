package skeleton.client;

/**
 * Created by michael on 03/12/15.
 */
public class Constants {
    public static final long EXTRA_SYNC_TIME = 200; //0.2s
    public static final int SYNC_WAITING_WINDOW = -10; //how long to wait
    public static final int MAX_FAILURES = 3;

    public enum MODE {
        IDLE,
        MOVIE,
        AUTO
    };
}
