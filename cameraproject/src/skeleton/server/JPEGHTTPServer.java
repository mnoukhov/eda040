package skeleton.server;

/*
 * Real-time and concurrent programming
 *
 * Minimalistic HTTP server solution.
 *
 * Package created by Patrik Persson, maintained by klas@cs.lth.se
 * Adapted for Axis cameras by Roger Henriksson 
 */

import java.net.*;                  // Provides ServerSocket, Socket
import java.io.*;                   // Provides InputStream, OutputStream

import se.lth.cs.eda040.fakecamera.*;      // Provides AxisM3006V
import static skeleton.client.Constants.*;

/**
 * Itsy bitsy teeny weeny web server. Always returns an image, regardless
 * of the requested file name.
 */
public class JPEGHTTPServer {

	// ----------------------------------------------------------- MAIN PROGRAM

	public static void main(String[]args) {
		JPEGHTTPServer theServer = new JPEGHTTPServer(Integer.parseInt(args[0]));
		try {
			theServer.handleRequests();
		} catch(IOException e) {
			System.out.println("Error!");
			theServer.destroy();
			System.exit(1);
		}
	}

	// ------------------------------------------------------------ CONSTRUCTOR

	/**
	 * @param   port   The TCP port the server should listen to
	 */
	public JPEGHTTPServer(int port) {
		myPort   = port;
		myCamera = new AxisM3006V();
		myCamera.init();
		myCamera.setProxy("argus-1.student.lth.se", port);
	}

	// --------------------------------------------------------- PUBLIC METHODS

	/**
	 * This method handles client requests. Runs in an eternal loop that does
	 * the following:
	 * <UL>
	 * <LI>Waits for a client to connect
	 * <LI>Reads a request from that client
	 * <LI>Sends a JPEG image from the camera (if it's a GET request)
	 * <LI>Closes the socket, i.e. disconnects from the client.
	 * </UL>
	 *
	 * Two simple help methods (getLine/putLine) are used to read/write
	 * entire text lines from/to streams. Their implementations follow below.
	 */
	public synchronized void handleRequests() throws IOException {
		byte[] jpeg = new byte[AxisM3006V.IMAGE_BUFFER_SIZE];
		ServerSocket serverSocket = new ServerSocket(myPort);
        System.out.println("HTTP server operating at port " + myPort + ".");
        Socket clientSocket = serverSocket.accept();
        OutputStream os = clientSocket.getOutputStream();

		while (true) {
            try {
                wait(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
			try {
				// The 'accept' method waits for a client to connect, then
				// returns a socket connected to that client.

				// The socket is bi-directional. It has an input stream to read
				// from and an output stream to write to. The InputStream can
				// be read from using read(...) and the OutputStream can be
				// written to using write(...). However, we use our own
				// getLine/putLine methods below.

                // Got a GET request. Respond with a JPEG image from the
                // camera. Tell the client not to cache the image
                putLine(os, "HTTP/1.0 200 OK");
                putLine(os, "Content-Type: image/jpeg");
                putLine(os, "Pragma: no-cache");
                putLine(os, "Cache-Control: no-cache");
                putLine(os, "");                   // Means 'end of header'

                if (!myCamera.connect()) {
                    System.out.println("Failed to connect to camera!");
                    System.exit(1);
                }
                int len = myCamera.getJPEG(jpeg, 0);
                putLine(os, CMD_JPEG);
                putLine(os, Integer.toString(len));
                os.write(jpeg, 0, len);
                System.out.println("Image sent: length " + len);
			}
			catch (IOException e) {
				System.out.println("Caught exception " + e);
                break;
			}
		}
        myCamera.close();
        clientSocket.close();	          // Disconnect from the client
        os.flush();                      // Flush any remaining content
	}
	
	public void destroy() {
		myCamera.destroy();
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

	/**
	 * Send a line on OutputStream 's', terminated by CRLF. The CRLF should not
	 * be included in the string str.
	 */
	private static void putLine(OutputStream s, String str)
			throws IOException {
		s.write(str.getBytes());
		s.write(CRLF);
	}

    // get n input bytes
    private static byte[] getInputBytes(InputStream is, int n) {
        byte[] data = new byte[n];
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
                System.out.println("read: " + bytesRead);
                if (status > 0) {
                    bytesRead += status;
                    bytesLeft -= status;
                }
            } while (bytesLeft > 0);

//            System.out.println("Received image data ("
//                    + bytesRead + " bytes).");

        } catch (IOException e) {
            System.out.println("Error when receiving bytes.");
        }

        return data;
    }

	// ----------------------------------------------------- PRIVATE ATTRIBUTES

	private int myPort;                             // TCP port for HTTP server
	private AxisM3006V myCamera;                    // Makes up the JPEG images

	// By convention, these bytes are always sent between lines
	// (CR = 13 = carriage return, LF = 10 = line feed)

	private static final byte[] CRLF      = { 13, 10 };
}