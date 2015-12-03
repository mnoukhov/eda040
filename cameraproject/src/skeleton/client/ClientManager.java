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
public class ClientManager {
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

        new ClientManager(addressPort1, addressPort2);
	}

    /**
     * Konstruktor för klient. Skapar GUI, pictureHandler.
     * Två PictureUpdateThread och två PictureQueue.
     */
    public ClientManager(String[] c1ap, String[] c2ap) {
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

    public synchronized void setMode(MODE m) {
        this.mode = m;
    }

    public synchronized boolean isConnected() {
        return connected;
    }

    public synchronized void connectButton() {
        boolean errored = false;

        if (connected) {
            System.out.println("Disconnect");

        } else {
            System.out.println("Connect");
            try {
                camera1Output = connect(camera1AddressPort);
            } catch (IOException e) {
                System.err.println("Camera 1 did not connect");
                e.printStackTrace();
                errored = true;
            }

            if (!errored) {
                connected = true;
                System.out.println("Connected");
            }
        }
    }

    public synchronized OutputStream connect(String[] CameraAddressPort) throws IOException {
        String address = CameraAddressPort[0];
        int port = Integer.parseInt(CameraAddressPort[1]);

        Socket cameraSocket = new Socket(address, port);
        this.serverInput = new ServerInput(
                this,
                cameraSocket.getInputStream(),
                camera1ImageQ
        );
        System.out.println("Server input started");
//        this.serverInput = new ServerInput(
//                this,
//                gui.getImagePanel(),
//                address,
//                port
//        );
        this.serverInput.start();

//        return cameraSocket.getOutputStream();
        return null;
    }
}
