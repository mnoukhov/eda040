package skeleton.camera;

import se.lth.cs.eda040.fakecamera.AxisM3006V;
//import se.lth.cs.eda040.proxycamera.AxisM3006V;
import se.lth.cs.realtime.RTThread;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by michael on 03/12/15.
 */
public class CameraManager extends RTThread {
    private int port;
    private AxisM3006V camera;
    private CameraMonitor cameraMonitor;
    private Camera cameraThread;

    private InputStream is;

    public static void main (String[] args) {
        CameraManager cm = new CameraManager(Integer.parseInt(args[0]));
        cm.start();
    }

    public CameraManager(int serverPort) {
        super();
        port = serverPort;
        camera = new AxisM3006V();
        camera.init();
        camera.setProxy("argus-1.student.lth.se", port);
        cameraMonitor = new CameraMonitor();
    }

    public void run() {
        String cmd;
        try {
            camera.connect();
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("HTTP server operating at port " + port + ".");

            while (true) {
                try {
                    System.out.println("Server waiting for client");
                    Socket clientSocket = serverSocket.accept();
                    is = clientSocket.getInputStream();
                    cameraMonitor.setOutputStream(clientSocket.getOutputStream());
                    cameraMonitor.setConnected(true);
                    System.out.println("Server connected to client");
                    cameraThread = new Camera(cameraMonitor, camera);
                    cameraThread.start();

                    while (cameraMonitor.isConnected()) {
                        String responseLine;
                        responseLine = getLine(is);
                        System.out.println("Client sends '" + responseLine + "'.");
                        // Ignore the following header lines up to the final empty one.
                        do {
                            responseLine = getLine(is);
                        } while (!(responseLine.equals("")));

                        cmd = getLine(is);

                        if (cmd.equals(Constants.CMD_IDLE)) {
                            System.out.println("Camera on " + port +" CMD IDLE");
                            cameraMonitor.setMode(Constants.MODE.IDLE);
                        } else if (cmd.equals(Constants.CMD_MOVIE)) {
                            cameraMonitor.setMode(Constants.MODE.MOVIE);
                            System.out.println("Camera on " + port +" CMD MOVIE");
                        } else if (cmd.equals(Constants.CMD_AUTO)) {
                            cameraMonitor.setMode(Constants.MODE.AUTO);
                            System.out.println("Camera on " + port +" CMD AUTO");
                        } else if (cmd.equals(Constants.CMD_DISCONNECT)) {
                            System.out.println("Camera on " + port +" CMD DISCONNECT");
                            cameraMonitor.setConnected(false);
                            cameraThread.terminate();
                        } else if (cmd.equals(Constants.CMD_SHUTDOWN)) {
                            System.out.println("Camera on " + port +" CMD SHUTDOWN");
                            cameraMonitor.setConnected(false);
                            cameraThread.terminate();
                            camera.destroy();
                            System.exit(0);
                        } else {
                            System.err.println("Camera on " + port + "unrecognized command " + cmd);
                        }
                    }
                    System.out.println("disconnecting server");
                    cameraMonitor.flushOS();
                    clientSocket.close();
                } catch (IOException e) {
                    System.out.println("Failed to accept socket or failed to get outputstream");
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to connect to socket");
            e.printStackTrace();
        }
        camera.close();
    }

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
}
