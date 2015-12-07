package cameraproject.client;

import static cameraproject.client.Constants.*;

import se.lth.cs.eda040.fakecamera.AxisM3006V;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;

/**
 * Created by michael on 02/12/15.
 */
public class ServerInput extends Thread {
    ClientMonitor c;
    InputStream is;
    BlockingQueue<Image> imageQueue;
    String cmd;
    int cameraNum;

    public ServerInput(ClientMonitor c, InputStream serverInput, BlockingQueue<Image> imageQueue, int cameraNum) {
        this.c = c;
        this.is = serverInput;
        this.imageQueue = imageQueue;
        this.cameraNum = cameraNum;
    }

    public void run() {
        byte[] jpeg = new byte[AxisM3006V.IMAGE_BUFFER_SIZE];
        byte[] timestamp = new byte[8];

        while (c.isConnected()) {
            try {
                // Read the first line of the response (status line)
                String responseLine;
                responseLine = getLine(is);
                System.out.println("HTTP Server sends '" + responseLine + "'.");
                // Ignore the following header lines up to the final empty one.
                do {
                    responseLine = getLine(is);
                } while (!(responseLine.equals("")));


                cmd = getLine(is);

                if (cmd.equals(CMD_JPEG)) {
                    int len = Integer.parseInt(getLine(is));
                    System.out.println("Reading image size " + len);
                    getInputBytes(is, jpeg, len);
                    getInputBytes(is, timestamp, 8);
                    imageQueue.add(new Image(jpeg, timestamp));
                } else if (cmd.equals(CMD_MOVIE)) {
                    System.out.println("CMD: movie");
                    c.setMode(MODE.MOVIE, cameraNum);
                } else {
                    System.err.println("Client unrecognized command " + cmd);
                }
            } catch (IOException e) {
                System.err.println("Error when receiving image.");
                break;
            }
        }
    }

    // -------------------------------------------------------- PRIVATE METHODS
    /**
     * Read a line from InputStream 's', terminated by CRLF. The CRLF is
     * not included in the returned string.
     */
    private static String getLine(InputStream s)
            throws IOException {
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

    // get n input bytes
    private static void getInputBytes(InputStream is, byte[] data, int n) {
        try {
            int bytesRead = 0;
            int bytesLeft = n;
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
            } while (bytesLeft > 0);

//            System.out.println("Received data (" + bytesRead + " bytes).");

        } catch (IOException e) {
            System.out.println("Error when receiving bytes.");
        }
    }
}
