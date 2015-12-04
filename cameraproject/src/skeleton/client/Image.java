package skeleton.client;

import static skeleton.client.Constants.*;

public class Image implements Comparable<Image> {
    byte[] jpeg;
    long timestamp;
    long timeToDisplay;
    long delay;

    public Image (byte[] data, byte[] ts) {
        this.jpeg = data;
        this.timestamp = getTimestampFromBytes(ts);
        this.delay = System.currentTimeMillis() - timestamp;
        this.timeToDisplay = timestamp + EXTRA_SYNC_TIME - delay;
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

    private long getTimestampFromBytes(byte[] bytes) {
        long value = 0;
        for (int i = 0; i < bytes.length; i++)
        {
            value = (value << 8) + (bytes[i] & 0xff);
        }
        return value;
    }
}
