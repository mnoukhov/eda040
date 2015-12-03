package skeleton.client;

import static skeleton.client.Constants.*;

import java.util.Arrays;

public class Image implements Comparable<Image> {
    byte[] jpeg;
    long timestamp;
    long timeToDisplay;
    long delay;

    public Image (byte[] data) {
        this.jpeg = data;
//        this.timestamp =
//        this.delay = System.currentTimeMillis() - timestamp;
//        this.timeToDisplay = timestamp + EXTRA_SYNC_TIME - delay;
    }

    public long getTimeToDisplay() {
        return timeToDisplay;
    }

    public byte[] getJpeg() {
        return jpeg;
    }

    public int compareTo(Image other) {
        return (int) (this.timestamp - other.timestamp);
    }
}
