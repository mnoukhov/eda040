package skeleton.client;

import java.io.OutputStream;
import java.util.Queue;

/**
 * Created by michael on 02/12/15.
 */
public class Client {
    String[] camera1AddressPort;
    String[] camera2AddressPort;
    private ServerInput serverInput;
    private OutputStream camera1Output;
    private OutputStream camera2Output;
    private boolean connected = false;
    private GUI gui;
//    private MODE mode;
//    private Queue<Picture> camera1PictureQ;
//    private Queue<Picture> camera2PictureQ;
//    private PictureOutput camera1PictureOutput;
//    private PictureOutput camera2PictureOutput;


    /**
     * Konstruktor för klient. Skapar GUI, pictureHandler.
     * Två PictureUpdateThread och två PictureQueue.
     */
    public Client(String[] c1ap, String[] c2ap) {
        this.camera1AddressPort = c1ap;
        this.camera2AddressPort = c2ap;

        this.gui = new GUI();
        this.serverInput = new ServerInput(camera1AddressPort[0],Integer.parseInt(camera1AddressPort[1]));


//        pT1 = new PictureUpdateThread(gui, picQueue1, 1);
//        pT2 = new PictureUpdateThread(gui, picQueue2, 2);
//        pT1.start();
//        pT2.start();
    }

}
