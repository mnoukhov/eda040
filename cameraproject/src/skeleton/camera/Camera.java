package skeleton.camera;

//import se.lth.cs.eda040.fakecamera.AxisM3006V;
import se.lth.cs.eda040.proxycamera.AxisM3006V;
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
        while (cameraMonitor.isConnected()) {
            byte[] jpeg = new byte[AxisM3006V.IMAGE_BUFFER_SIZE];
            byte[] timestamp = new byte[8];
            int length = camera.getJPEG(jpeg, 0);
            camera.getTime(timestamp, 0);
            if (length < 0) {
                // getJpeg messed up, try again
                // this is a symptom of a bad connection, usually will break pipe right after
                System.err.print("getJPEG returned negative length, try again");
                continue;
            }
            cameraMonitor.sendImageToClient(jpeg, length, timestamp, camera.motionDetected());
        }
    }
}


