package skeleton.server;

import static skeleton.client.Constants.*;

import se.lth.cs.eda040.fakecamera.AxisM3006V;
import se.lth.cs.realtime.RTThread;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

/**
 * Created by michael on 03/12/15.
 */
public class CameraManager extends RTThread {
    private int port;
    private AxisM3006V camera;
    private CameraMonitor cameraMonitor;
    private Camera cameraThread;

    private InputStream is;
    private boolean connected;

    public static void main (String[] args) {
        CameraManager cm = new CameraManager(Integer.parseInt(args[0]));
        cm.start();
    }

    public CameraManager(int serverPort) {
        super();
        port = serverPort;
        cameraMonitor = new CameraMonitor();
        camera = new AxisM3006V();
        camera.init();
        camera.setProxy("argus-1.student.lth.se", port);
    }

    public void run() {
        try {
            camera.connect();
            byte[] cmd = new byte[4];
            byte[] jpeg = new byte[AxisM3006V.IMAGE_BUFFER_SIZE];
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("HTTP server operating at port " + port + ".");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                is = clientSocket.getInputStream();
                cameraMonitor.setOutputStream(clientSocket.getOutputStream());
                connected = true;
                System.out.println("Server connected to client");
                cameraThread = new Camera(cameraMonitor, camera);
                cameraThread.start();

                while (connected) {
                    getInputBytes(cmd);
                    System.out.println("server got cmd");

                    if (Arrays.equals(cmd, CMD_IDLE)) {
                        cameraMonitor.setMode(MODE.IDLE);
                    } else if (Arrays.equals(cmd, CMD_MOVIE)) {
                        cameraMonitor.setMode(MODE.MOVIE);
                    } else if (Arrays.equals(cmd, CMD_DISCONNECT)) {
                        connected = false;
                        cameraThread.stop();
                    } else {
                        System.err.println("Unrecognized command " + cmd);
                    }
                }
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        camera.close();
    }

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
}
