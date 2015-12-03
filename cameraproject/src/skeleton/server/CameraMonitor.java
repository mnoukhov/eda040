package skeleton.server;

import static skeleton.client.Constants.*;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by michael on 03/12/15.
 */
public class CameraMonitor {
    private OutputStream os;
    private MODE mode;

    public CameraMonitor() {}

    public synchronized void setOutputStream(OutputStream os) {
        this.os = os;
    }

    public synchronized void sendImageToClient(byte[] msg, int len) {
        try {
            putLine(os, "HTTP/1.0 200 OK");
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

    public synchronized void setMode(MODE m) {
        this.mode = m;
    }

    public synchronized void flushOS() {
        try {
            this.os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final byte[] CRLF      = { 13, 10 };

    private void putLine(OutputStream s, String str)
            throws IOException {
        s.write(str.getBytes());
        s.write(CRLF);
    }
}
