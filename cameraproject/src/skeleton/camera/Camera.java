package skeleton.camera;

import se.lth.cs.eda040.fakecamera.AxisM3006V;
//import se.lth.cs.eda040.proxycamera.AxisM3006V;
import se.lth.cs.realtime.RTThread;

/**
 * Created by michael on 03/12/15.
 */
public class Camera extends RTThread {
    CameraMonitor cameraMonitor;
    AxisM3006V camera;

    public Camera(CameraMonitor cameraMonitor, AxisM3006V camera) {
        this.cameraMonitor = cameraMonitor;
        this.camera = camera;
    }

    public void run() {
        while (true) {
            byte[] bytes = new byte[AxisM3006V.IMAGE_BUFFER_SIZE + 8];
            camera.getTime(bytes, 0);
            int length = camera.getJPEG(bytes, 8);
            cameraMonitor.sendImageToClient(bytes, length + 8, camera.motionDetected());
        }
    }
}


