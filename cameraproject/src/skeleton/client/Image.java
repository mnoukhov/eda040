package skeleton.client;

import java.util.Arrays;

public class Image implements Comparable<Image> {
    private final long EXTRA_TIME = 200;

    byte[] jpeg;
    long timestamp;
    long timeToDisplay;
    long delay;

    public Image (byte[] data) {
        this.jpeg = data;
//        this.timestamp =
//        this.delay = System.currentTimeMillis() - timestamp;
//        this.timeToDisplay = timestamp + EXTRA_TIME - delay;
    }

    public int compareTo(Image other) {
        return (int) (this.timestamp - other.timestamp);
    }
}
