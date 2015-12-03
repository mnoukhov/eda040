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

    public synchronized void sendImageToClient(byte[] jpeg) {
        try {
            os.write(CMD_JPEG);
            os.write(CRLF);
            os.write(jpeg);
            os.write(CRLF);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void setMode(MODE m) {
        this.mode = m;
    }

    private final byte[] CRLF      = { 13, 10 };
}
