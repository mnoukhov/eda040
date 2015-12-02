package skeleton.client;

import jdk.nashorn.internal.ir.Block;

import java.io.OutputStream;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
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
//    private MODE mode;
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


//        pT1 = new PictureUpdateThread(gui, picQueue1, 1);
//        pT2 = new PictureUpdateThread(gui, picQueue2, 2);
//        pT1.start();
//        pT2.start();
    }

    public synchronized void setSync(boolean sync) {
        this.sync = sync;
    }

    public synchronized boolean isSync() {
        return sync;
    }

}
