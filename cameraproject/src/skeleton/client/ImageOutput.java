package skeleton.client;

import java.util.concurrent.BlockingQueue;

public class ImageOutput extends Thread {
    private Client c;
    private ImagePanel imagePanel;
    private BlockingQueue<Image> imageQueue;

    public ImageOutput(Client c, ImagePanel ip, BlockingQueue<Image> iq) {
        this.c = c;
        this.imagePanel = ip;
        this.imageQueue = iq;
    }

    public void run() {
        while (true) {
            try {
                Image next = imageQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (c.isSync()) {
                c.
            }
        }
    }
}
