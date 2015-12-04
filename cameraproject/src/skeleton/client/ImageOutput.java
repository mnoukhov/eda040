package skeleton.client;

import static skeleton.client.Constants.*;

import java.util.concurrent.BlockingQueue;

public class ImageOutput extends Thread {
    private ClientMonitor c;
    private ImagePanel imagePanel;
    private BlockingQueue<Image> imageQueue;

    public ImageOutput(ClientMonitor c, ImagePanel ip, BlockingQueue<Image> iq) {
        this.c = c;
        this.imagePanel = ip;
        this.imageQueue = iq;
    }

    public void run() {
        Image next;
        boolean success;
        int failures = 0;
        while (true) {
            try {
                next = imageQueue.take();
                success = c.maybeSyncUntil(next.getTimeToDisplay());

                if (!success) {
                    failures += 1;
                } else {
                    failures = 0;
                }

                if (failures > MAX_FAILURES) {
                    c.setSync(false);
                    failures = 0;
                }

                imagePanel.refresh(next.getJpeg());

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
