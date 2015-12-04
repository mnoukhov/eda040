package skeleton.server;

import static skeleton.client.Constants.*;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by michael on 03/12/15.
 */
public class CameraMonitor {
    private OutputStream os;
    private MODE mode = MODE.IDLE;

    public CameraMonitor() {}

    public synchronized void setOutputStream(OutputStream os) {
        this.os = os;
    }

    public synchronized void sendImageToClient(byte[] msg, int len) {
        try {
            putLine(os, "POST /image.jpg HTTP/1.0");
            putLine(os, "Content-Type: image/jpeg");
            putLine(os, "Pragma: no-cache");
            putLine(os, "Cache-Control: no-cache");
            putLine(os, "");                   // Means 'end of header'
            putLine(os, CMD_JPEG);
            putLine(os, Integer.toString(len));
            os.write(msg, 0, len);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void sendMovieChangeToClient() {
        try {
            putLine(os, "POST HTTP/1.0");
            putLine(os, "Content-Type: text");
            putLine(os, "");                   // Means 'end of header'
            putLine(os, CMD_MOVIE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void flushOS() {
        try {
            this.os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void setMode(MODE mode) {
        this.mode = mode;
    }

    private static final byte[] CRLF      = { 13, 10 };

    private static void putLine(OutputStream s, String str)
            throws IOException {
        s.write(str.getBytes());
        s.write(CRLF);
    }
}
