package skeleton.client;

import static skeleton.client.Constants.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by michael on 02/12/15.
 */
public class ClientMonitor {
    private String[] camera1AddressPort;
    private String[] camera2AddressPort;
    private ServerInput serverInput;
    private OutputStream camera1Output;
    private OutputStream camera2Output;
    private boolean connected = false;
    private boolean sync = false;
    private GUI gui;
    private MODE mode;
    private BlockingQueue<Image> camera1ImageQ;
    private BlockingQueue<Image> camera2ImageQ;
    private ImageOutput camera1ImageOutput;
    private ImageOutput camera2ImageOutput;


	public static void main(String[] args) {
		if (args.length!=4) {
			System.out.println("Syntax: JPEGHTTPClient <address1> <port1> <address2> <port2>");
			System.exit(1);
		}
        String[] addressPort1 = Arrays.copyOfRange(args,0,2);
        String[] addressPort2 = Arrays.copyOfRange(args, 3, 4);

        new ClientMonitor(addressPort1, addressPort2);
	}

    public ClientMonitor(String[] c1ap, String[] c2ap) {
        this.camera1AddressPort = c1ap;
        this.camera2AddressPort = c2ap;
        this.camera1ImageQ = new PriorityBlockingQueue<Image>();
        this.camera2ImageQ = new PriorityBlockingQueue<Image>();
        this.gui = new GUI(this);

        this.camera1ImageOutput = new ImageOutput(
                this,
                this.gui.getImagePanel(),
                this.camera1ImageQ
        );
        this.camera1ImageOutput.start();

    }

    public synchronized void setSync(boolean sync) {
        this.sync = sync;
    }

    // maybe sync the image and then return true if everything worked, false if tried to sync but could not
    public synchronized boolean maybeSyncUntil(long displayTime) {
        if (sync) {
            long waitTime;
            do {
                waitTime = displayTime - System.currentTimeMillis();
                try {
                    wait(waitTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (waitTime > 0);

            if (waitTime < SYNC_WAITING_WINDOW) {
                return false; // we waited too long!
            }
        }
        return true;
    }

    // source = 0 if from user
    // 1 if from camera 1
    // 2 if from camera 2
    public synchronized void setMode(MODE mode, int source) {
        if (this.mode == mode) {
            return;
        }

        this.mode = mode;
        try {
            if (source != 1) {
                sendModeChangeToServer(camera1Output, mode);
            }
            if (source != 2) {
//                sendModeChangeToServer(camera2Output, mode);
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public synchronized void sendModeChangeToServer(OutputStream os, MODE mode) throws IOException {
        putLine(os, "POST HTTP/1.0");
        putLine(os, "Content-Type: text");
        putLine(os, "");                   // Means 'end of header'
        if (mode == MODE.MOVIE) {
            putLine(os, CMD_MOVIE);
        } else if (mode == MODE.IDLE) {
            putLine(os, CMD_IDLE);
        } else if (mode == MODE.AUTO) {
            putLine(os, CMD_IDLE);
        }
    }

    public synchronized boolean isConnected() {
        return connected;
    }

    public synchronized boolean connectButton() {
        boolean success = true;

        if (connected) {
            System.out.println("Disconnect");
            disconnect();
        } else {
            System.out.println("Connect");
            try {
                camera1Output = connect(camera1AddressPort);
            } catch (IOException e) {
                System.err.println("Camera 1 did not connect");
                e.printStackTrace();
                success = false;
            }

//            try {
//                camera2Output = connect(camera2AddressPort);
//            } catch (IOException e) {
//                System.err.println("Camera 2 did not connect");
//                e.printStackTrace();
//                success = false;
//            }

            if (success) {
                connected = true;
                System.out.println("Connected");
            }
        }
        return success;
    }

    public synchronized void idleButton() {
        setMode(MODE.IDLE, 0);
    }

    public synchronized void movieButton() {
        setMode(MODE.MOVIE, 0);
    }

    public synchronized void autoButton() {
        setMode(MODE.AUTO, 0);
    }

    public synchronized boolean syncButton() {
        sync = !sync;   //toggle sync
        return true;
    }

    // PRIVATE METHODS

    private synchronized OutputStream connect(String[] CameraAddressPort) throws IOException {
        String address = CameraAddressPort[0];
        int port = Integer.parseInt(CameraAddressPort[1]);

        Socket cameraSocket = new Socket(address, port);
        this.serverInput = new ServerInput(
                this,
                cameraSocket.getInputStream(),
                camera1ImageQ,
                1
        );
        System.out.println("Server input started");
        this.serverInput.start();

        return cameraSocket.getOutputStream();
    }

    private synchronized void disconnect() {
        try {
            putLine(camera1Output, CMD_DISCONNECT);
            putLine(camera2Output, CMD_DISCONNECT);
        } catch (IOException e) {
            System.out.println("disconnect failed");
        }

        camera1ImageQ.clear();
        camera2ImageQ.clear();

        connected = false;
        mode = MODE.AUTO;
        sync = false;
    }

    // STATIC METHODS + VARS

    private static final byte[] CRLF      = { 13, 10 };

    private static void putLine(OutputStream s, String str)
            throws IOException {
        s.write(str.getBytes());
        s.write(CRLF);
    }
}
