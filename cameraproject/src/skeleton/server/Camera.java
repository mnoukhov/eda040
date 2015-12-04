package skeleton.server;

import static skeleton.client.Constants.*;
import se.lth.cs.eda040.fakecamera.AxisM3006V;
import se.lth.cs.realtime.PeriodicThread;

/**
 * Created by michael on 03/12/15.
 */
public class Camera extends PeriodicThread {
    CameraMonitor cameraMonitor;
    AxisM3006V camera;

    public Camera(CameraMonitor cameraMonitor, AxisM3006V camera) {
        super(IDLE_PERIOD);
        this.cameraMonitor = cameraMonitor;
        this.camera = camera;
    }

    public void perform() {
        //TODO: add timestamp
        byte[] bytes = new byte[AxisM3006V.IMAGE_BUFFER_SIZE];
        int length = camera.getJPEG(bytes, 0);
        cameraMonitor.sendImageToClient(bytes, length);

        if (camera.motionDetected()) {
            setPeriod(MOVIE_PERIOD);
            cameraMonitor.sendMovieChangeToClient();
        }
    }
}


