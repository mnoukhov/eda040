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
        String[] addressPort2 = Arrays.copyOfRange(args,2,4);

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
                this.gui.getImagePanel(1),
                this.camera1ImageQ
        );
        this.camera1ImageOutput.start();

        this.camera2ImageOutput = new ImageOutput(
                this,
                this.gui.getImagePanel(2),
                this.camera1ImageQ
        );
        this.camera2ImageOutput.start();
    }

    public synchronized void setSync(boolean sync) {
        this.sync = sync;
        gui.setSyncModeLabel(sync);
    }

    // maybe sync the image and then return true if everything worked, false if tried to sync but could not
    public synchronized boolean maybeSyncUntil(long displayTime) {
        if (sync) {
            long waitTime = displayTime - System.currentTimeMillis();
            while (waitTime > 0) {
                try {
                    wait(waitTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                waitTime = displayTime - System.currentTimeMillis();
            }

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
        gui.setSourceLabel(source);
        gui.setDisplayModeLabel(mode);
        try {
            if (source != 1) {
                sendModeChangeToServer(camera1Output, mode);
            }
            if (source != 2) {
                sendModeChangeToServer(camera2Output, mode);
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public synchronized void sendModeChangeToServer(OutputStream os, MODE mode) throws IOException {
        putLine(os, "POST mode-change HTTP/1.0");
        putLine(os, "Content-Type: text");
        putLine(os, "");                   // Means 'end of header'
        if (mode == MODE.MOVIE) {
            putLine(os, CMD_MOVIE);
        } else if (mode == MODE.IDLE) {
            putLine(os, CMD_IDLE);
        } else if (mode == MODE.AUTO) {
            putLine(os, CMD_AUTO);
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
                camera1Output = connect(camera1AddressPort, camera1ImageQ, 1);
            } catch (IOException e) {
                System.err.println("Camera 1 did not connect");
                e.printStackTrace();
                success = false;
            }

            try {
                camera2Output = connect(camera2AddressPort, camera2ImageQ, 2);
            } catch (IOException e) {
                System.err.println("Camera 2 did not connect");
                e.printStackTrace();
                success = false;
            }

            if (success) {
                connected = true;
                System.out.println("Client connected to server");
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
        setSync(!sync);
        return true;
    }

    public synchronized void windowClosing() {
        try {
            shutdown(camera1Output);
            shutdown(camera2Output);
        } catch (IOException e) {
            System.out.println("Shutdown failed");
        }
    }

    private synchronized void shutdown(OutputStream os) throws IOException {
        putLine(os, "POST mode-change HTTP/1.0");
        putLine(os, "Content-Type: text");
        putLine(os, "");                   // Means 'end of header'
        putLine(os, CMD_SHUTDOWN);
    }

    // PRIVATE METHODS

    private synchronized OutputStream connect(String[] CameraAddressPort, BlockingQueue<Image> imageQueue, int cameraNum) throws IOException {
        String address = CameraAddressPort[0];
        int port = Integer.parseInt(CameraAddressPort[1]);

        Socket cameraSocket = new Socket(address, port);
        ServerInput serverInput = new ServerInput(
                this,
                cameraSocket.getInputStream(),
                imageQueue,
                cameraNum
        );
        System.out.println("Server input started");
        serverInput.start();

        return cameraSocket.getOutputStream();
    }

    private synchronized void disconnect() {
        try {
            putLine(camera1Output, CMD_DISCONNECT);
            putLine(camera2Output, CMD_DISCONNECT);
        } catch (IOException e) {
            System.out.println("disconnect failed");
            return;
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
