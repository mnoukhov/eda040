package cameraproject.main;

import cameraproject.client.ClientMonitor;
import cameraproject.camera.CameraManager;
import cameraproject.test.JPEGHTTPClient;

public class Main {

	public static void main(String[] args) {
        String localhostPort1 = "6077";
        String localhostPort2 = "6088";

        if (args.length != 4 && args.length != 6) {
            System.err.println("Syntax: ProxySecurityCam <camera1 host address number> <camera1 host port> <camera2 host address number> <camera2 host port> OPTIONAL: <client1 localhost port> <client2 localhost port>");
            System.exit(1);
        } else if (args.length == 6) {
            localhostPort1 = args[5];
            localhostPort2 = args[6];
        }
		CameraServer s1 = new CameraServer(args[0], args[1], localhostPort1);
		s1.start();
        CameraServer s2 = new CameraServer(args[2], args[3], localhostPort2);
		s2.start();
        TestClient tc = new TestClient(localhostPort1);
        tc.start();
		try {
			Thread.currentThread().sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Client c = new Client(localhostPort1, localhostPort2);
		c.start();
	}
	
	private static class CameraServer extends Thread {
        String cameraServer;
        String cameraServerPort;
        String localhostPort;

        private CameraServer(String cameraServerNum, String cameraServerPort, String localhostPort) {
            this.cameraServer = "argus-" + cameraServerNum + ".student.lth.se";
            this.cameraServerPort = cameraServerPort;
            this.localhostPort = localhostPort;
        }

		public void run() {
			CameraManager.main(new String[]{cameraServer, cameraServerPort, localhostPort});
		}
	}

    private static class TestClient extends Thread {
        String localhostPort;

        private TestClient(String localhostPort) {
            this.localhostPort = localhostPort;
        }

        public void run() {
            JPEGHTTPClient.main(new String[]{"localhost", localhostPort});
        }
    }
	
	private static class Client extends Thread {
        String localhostPort1;
        String localhostPort2;

        private Client(String localhostPort1, String localhostPort2) {
            this.localhostPort1 = localhostPort1;
            this.localhostPort2 = localhostPort2;
        }
		public void run() {
            ClientMonitor.main(new String[]{"localhost", localhostPort1, "localhost", localhostPort2});
		}
	}
}
