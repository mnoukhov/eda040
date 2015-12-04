package skeleton.camera;

import static skeleton.camera.Constants.*;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by michael on 03/12/15.
 */
public class CameraMonitor {
    private OutputStream os;
    private Constants.MODE mode = Constants.MODE.AUTO;
    private Camera cameraThread;
    private boolean connected = false;

    public CameraMonitor() {
    }

    public synchronized void setOutputStream(OutputStream os) {
        this.os = os;
    }

    public synchronized void setCameraThread(Camera cameraThread) {
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
            putLine(os, Constants.CMD_JPEG);
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
            putLine(os, Constants.CMD_MOVIE);
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

    public synchronized void setMode(Constants.MODE mode) {
        this.mode = mode;
        if (mode == Constants.MODE.IDLE || mode == Constants.MODE.AUTO) {
            cameraThread.setPeriod(Constants.IDLE_PERIOD);
        } else if (mode == Constants.MODE.MOVIE) {
            cameraThread.setPeriod(Constants.MOVIE_PERIOD);
        }
    }

    public synchronized Constants.MODE getMode() {
        return mode;
    }

    public synchronized void setConnected(boolean connected) {
        this.connected = connected;
    }

    public synchronized boolean isConnected() {
        return connected;
    }

    private synchronized void waitFor(long timeout) throws InterruptedException {
        long tf = System.currentTimeMillis() + timeout;
        while ((timeout = tf - System.currentTimeMillis()) > 0) wait(timeout);
    }

    private int waitForMode(MODE mode) {
       if (mode == MODE.MOVIE) {
           return MOVIE_PERIOD;
       } else {
           return IDLE_PERIOD;
       }
    }

    private static final byte[] CRLF      = { 13, 10 };

    private static void putLine(OutputStream s, String str)
            throws IOException {
        s.write(str.getBytes());
        s.write(CRLF);
    }
}
