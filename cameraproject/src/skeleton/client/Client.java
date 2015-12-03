package skeleton.client;

import static skeleton.client.Constants.*;

import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by michael on 02/12/15.
 */
public class Client {
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


    /**
     * Konstruktor för klient. Skapar GUI, pictureHandler.
     * Två PictureUpdateThread och två PictureQueue.
     */
    public Client(String[] c1ap, String[] c2ap) {
        this.camera1AddressPort = c1ap;
        this.camera2AddressPort = c2ap;
        this.camera1ImageQ = new PriorityBlockingQueue<Image>();
        this.camera2ImageQ = new PriorityBlockingQueue<Image>();

        this.serverInput = new ServerInput(this,
                camera1AddressPort[0],
                Integer.parseInt(camera1AddressPort[1]),
                camera1ImageQ
        );
        this.serverInput.start();

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

//    public synchronized connect(String[] addressPort) {
//
//    }
}
