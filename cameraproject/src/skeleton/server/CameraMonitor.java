package skeleton.server;

import static skeleton.server.Constants.*;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by michael on 03/12/15.
 */
public class CameraMonitor {
    private OutputStream os;
    private MODE mode = MODE.AUTO;
    private Camera cameraThread;
    private boolean connected = false;

    public CameraMonitor() {}

    public synchronized void setOutputStream(OutputStream os) {
        this.os = os;
    }

    public synchronized void setCameraThread (Camera cameraThread) {
        this.cameraThread = cameraThread;
    }

    public synchronized void sendImageToClient(byte[] msg, int len) {
        if (!connected) {
            return;
        }

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
        if (!connected) {
            return;
        }

        try {
            putLine(os, "POST mode-change HTTP/1.0");
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
        if (mode == MODE.IDLE || mode == MODE.AUTO) {
            cameraThread.setPeriod(IDLE_PERIOD);
        } else if (mode == MODE.MOVIE) {
            cameraThread.setPeriod(MOVIE_PERIOD);
        }
    }

    public synchronized MODE getMode() {
        return mode;
    }

    public synchronized void setConnected(boolean connected) {
        this.connected = connected;
    }

    public synchronized boolean isConnected() {
        return connected;
    }

    private static final byte[] CRLF      = { 13, 10 };

    private static void putLine(OutputStream s, String str)
            throws IOException {
        s.write(str.getBytes());
        s.write(CRLF);
    }
}
