package cameraproject.camera;

import static cameraproject.camera.Constants.*;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by michael on 03/12/15.
 */
public class CameraMonitor {
    private OutputStream os;
    private MODE mode = MODE.AUTO;
    private boolean connected = false;

    public CameraMonitor() {
    }

    public synchronized void setOutputStream(OutputStream os) {
        this.os = os;
    }

    public synchronized void sendImageToClient(byte[] jpeg, int len, byte[] timestamp, boolean motionDetected) {
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
            os.write(jpeg, 0, len);
            os.write(timestamp, 0, 8);
        } catch (IOException e) {
            System.err.println("Error writing msg to OS, check that proxyserver is running");
            e.printStackTrace();
        }

        if (motionDetected && mode == MODE.AUTO) {
            mode = MODE.MOVIE;

            try {
                putLine(os, "POST mode-change HTTP/1.0");
                putLine(os, "Content-Type: text");
                putLine(os, "");                   // Means 'end of header'
                putLine(os, Constants.CMD_MOVIE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            waitForMode(mode);
        } catch (InterruptedException e) {
            System.err.println("Camera wait interrupted");
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
        notifyAll();
    }

    public synchronized void setConnected(boolean connected) {
        this.connected = connected;
        notifyAll();
    }

    public synchronized boolean isConnected() {
        return connected;
    }

    private synchronized void waitForMode(MODE mode) throws InterruptedException {
        long timeout;
        if (mode == MODE.MOVIE) {
            timeout = MOVIE_PERIOD;
        } else {
            timeout = IDLE_PERIOD;
        }

        wait(timeout);
    }

    private static final byte[] CRLF      = { 13, 10 };

    private static void putLine(OutputStream s, String str)
            throws IOException {
        s.write(str.getBytes());
        s.write(CRLF);
    }
}
