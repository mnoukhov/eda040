package skeleton.client;

import static skeleton.client.Constants.*;

import se.lth.cs.eda040.fakecamera.AxisM3006V;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;

/**
 * Created by michael on 02/12/15.
 */
public class ServerInput extends Thread {
    ClientManager c;
    InputStream is;
    BlockingQueue<Image> imageQueue;
    byte[] cmd = new byte[4];
    byte[] jpeg;

    public ServerInput(ClientManager c, InputStream serverInput, BlockingQueue<Image> imageQueue) {
        this.c = c;
        this.is = serverInput;
        this.imageQueue = imageQueue;
   }

    public void run() {
        System.out.println("Server input started");
        while (c.isConnected()) {
            getInputBytes(cmd);
//            System.out.println("got cmd");

            if (Arrays.equals(cmd, CMD_JPEG)) {
                jpeg = new byte[AxisM3006V.IMAGE_BUFFER_SIZE];
                getInputBytes(jpeg);
                imageQueue.add(new Image(jpeg));
            } else if (Arrays.equals(cmd, CMD_MOVIE)) {
                c.setMode(MODE.MOVIE);
            } else {
                System.err.println("Unrecognized command " + cmd);
            }
        }
    }
    // -------------------------------------------------------- PRIVATE METHODS

    // get n input bytes
    private void getInputBytes(byte[] data) {
        try {
            int bufferSize = data.length;
            int bytesRead = 0;
            int bytesLeft = bufferSize;
            int status;

            // We have to keep reading until -1 (meaning "end of file") is
            // returned. The socket (which the stream is connected to)
            // does not wait until all data is available; instead it
            // returns if nothing arrived for some (short) time.
            do {
                status = is.read(data, bytesRead, bytesLeft);
                // The 'status' variable now holds the no. of bytes read,
                // or -1 if no more data is available
                if (status > 0) {
                    bytesRead += status;
                    bytesLeft -= status;
                }
            } while (status >= 0);

            System.out.println("Received image data ("
                    + bytesRead + " bytes).");

        } catch (IOException e) {
            System.out.println("Error when receiving image.");
        }
    }

    private final byte[] CRLF      = { 13, 10 };

    /**
     * Read a line from InputStream 's', terminated by CRLF. The CRLF is
     * not included in the returned string.
     */
    private String getLine(InputStream s) throws IOException {
        boolean done = false;
        String result = "";

        while(!done) {
            int ch = s.read();        // Read
            if (ch <= 0 || ch == 10) {
                // Something < 0 means end of data (closed socket)
                // ASCII 10 (line feed) means end of line
                done = true;
            }
            else if (ch >= ' ') {
                result += (char)ch;
            }
        }

        return result;
    }

    /**
     * Send a line on OutputStream 's', terminated by CRLF. The CRLF should not
     * be included in the string str.
     */
    private void putLine(OutputStream s, String str) throws IOException {
        s.write(str.getBytes());
        s.write(CRLF);
    }
}
