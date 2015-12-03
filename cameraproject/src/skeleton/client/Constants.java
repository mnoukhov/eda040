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

    public static final byte[] CMD_JPEG = {0x0, 0x0, 0x0, 0x1};
    public static final byte[] CMD_IDLE = {0x0, 0x0, 0x1, 0x0};
    public static final byte[] CMD_MOVIE = {0x0, 0x1, 0x0, 0x0};
    public static final byte[] CMD_DISCONNECT = {0x1, 0x0, 0x0, 0x0};
}
