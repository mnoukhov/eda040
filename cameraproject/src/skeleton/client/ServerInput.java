package skeleton.client;

import se.lth.cs.eda040.fakecamera.AxisM3006V;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

/**
 * Created by michael on 02/12/15.
 */
public class ServerInput extends Thread {
    Client c;
    String server;
    int port;
    BlockingQueue<Image> imageQueue;
    byte [] jpeg = new byte[AxisM3006V.IMAGE_BUFFER_SIZE];

    public ServerInput(Client c, String server, int port, BlockingQueue<Image> imageQueue) {
        this.c = c;
        this.server = server;
        this.port = port;
        this.imageQueue = imageQueue;
    }

    public void run() {
        while (true) {
            try {
                // Open a socket to the server, get the input/output streams
                Socket sock = new Socket(server, port);
                InputStream is = sock.getInputStream();
                OutputStream os = sock.getOutputStream();

                // Send a simple request, always for "/image.jpg"
                putLine(os, "GET /image.jpg HTTP/1.0");
                putLine(os, "");        // The request ends with an empty line

                // Read the first line of the response (status line)
                String responseLine;
                responseLine = getLine(is);
                System.out.println("HTTP server says '" + responseLine + "'.");
                // Ignore the following header lines up to the final empty one.
                do {
                    responseLine = getLine(is);
                } while (!(responseLine.equals("")));

                // Now load the JPEG image.
                int bufferSize = jpeg.length;
                int bytesRead = 0;
                int bytesLeft = bufferSize;
                int status;

                // We have to keep reading until -1 (meaning "end of file") is
                // returned. The socket (which the stream is connected to)
                // does not wait until all data is available; instead it
                // returns if nothing arrived for some (short) time.
                do {
                    status = is.read(jpeg, bytesRead, bytesLeft);
                    // The 'status' variable now holds the no. of bytes read,
                    // or -1 if no more data is available
                    if (status > 0) {
                        bytesRead += status;
                        bytesLeft -= status;
                    }
                } while (status >= 0);
                sock.close();

                System.out.println("Received image data ("
                        + bytesRead + " bytes).");

            } catch (IOException e) {
                System.out.println("Error when receiving image.");
            }
            imageQueue.add(new Image(jpeg));
        }
    }
    // -------------------------------------------------------- PRIVATE METHODS

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
