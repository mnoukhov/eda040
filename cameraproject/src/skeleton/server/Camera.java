package skeleton.server;

import se.lth.cs.eda040.fakecamera.AxisM3006V;
import se.lth.cs.realtime.PeriodicThread;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by michael on 03/12/15.
 */
public class Camera extends PeriodicThread {
    CameraMonitor cameraMonitor;
    AxisM3006V camera;
//    SimpleViewer viewer;

    public Camera(CameraMonitor cameraMonitor, AxisM3006V camera) {
        super(1000);
        this.cameraMonitor = cameraMonitor;
        this.camera = camera;
//        this.viewer = new SimpleViewer();
    }

//    public void perform() {
//        //TODO: add timestamp
//        byte[] bytes = new byte[AxisM3006V.IMAGE_BUFFER_SIZE];
//        int length = camera.getJPEG(bytes, 0);
////        viewer.refreshImage(bytes);
//        cameraMonitor.sendImageToClient(bytes, length);
//        sleep(10000);
//    }

    public void perform() {
        try {
            byte[] jpeg = new byte[AxisM3006V.IMAGE_BUFFER_SIZE];
            ServerSocket serverSocket = new ServerSocket(myPort);
            System.out.println("HTTP server operating at port " + myPort + ".");

            while (true) {
                try {
                    // The 'accept' method waits for a client to connect, then
                    // returns a socket connected to that client.
                    Socket clientSocket = serverSocket.accept();

                    // The socket is bi-directional. It has an input stream to read
                    // from and an output stream to write to. The InputStream can
                    // be read from using read(...) and the OutputStream can be
                    // written to using write(...). However, we use our own
                    // getLine/putLine methods below.
                    InputStream is = clientSocket.getInputStream();
                    OutputStream os = clientSocket.getOutputStream();

                    // Read the request
                    String request = getLine(is);

                    // The request is followed by some additional header lines,
                    // followed by a blank line. Those header lines are ignored.
                    String header;
                    boolean cont;
                    do {
                        header = getLine(is);
                        cont = !(header.equals(""));
                    } while (cont);

                    System.out.println("HTTP request '" + request
                            + "' received.");

                    // Interpret the request. Complain about everything but GET.
                    // Ignore the file name.
                    if (request.substring(0, 4).equals("GET ")) {
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

                        os.write(jpeg, 0, len);
                        myCamera.close();
                    } else {
                        // Got some other request. Respond with an error message.
                        putLine(os, "HTTP/1.0 501 Method not implemented");
                        putLine(os, "Content-Type: text/plain");
                        putLine(os, "");
                        putLine(os, "No can do. Request '" + request
                                + "' not understood.");

                        System.out.println("Unsupported HTTP request!");
                    }

                    os.flush();                      // Flush any remaining content
                    clientSocket.close();              // Disconnect from the client
                } catch (IOException e) {
                    System.out.println("Caught exception " + e);
                }
            }
        } catch (IOException e) {
            System.out.println("connect failed?");
        }
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

    // ----------------------------------------------------- PRIVATE ATTRIBUTES

    private int myPort;                             // TCP port for HTTP server
    private AxisM3006V myCamera;                    // Makes up the JPEG images

    // By convention, these bytes are always sent between lines
    // (CR = 13 = carriage return, LF = 10 = line feed)

    private static final byte[] CRLF      = { 13, 10 };
}

//class SimpleViewer extends JFrame {
//    ImageIcon icon;
//    boolean firstCall = true;
//
//    public SimpleViewer() {
//        super();
//        getContentPane().setLayout(new BorderLayout());
//        icon = new ImageIcon();
//        JLabel label = new JLabel(icon);
//        add(label, BorderLayout.CENTER);
//        this.pack();
//        this.setSize(640, 480);
//        this.setVisible(true);
//    }
//
//    public void refreshImage(byte[] jpeg) {
//        Image image = getToolkit().createImage(jpeg);
//        getToolkit().prepareImage(image, -1, -1, null);
//        icon.setImage(image);
//        icon.paintIcon(this, this.getGraphics(), 0, 0);
//    }
//}



