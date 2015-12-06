package skeleton.demo;

import skeleton.client.ClientMonitor;
import skeleton.camera.CameraManager;
import skeleton.test.JPEGHTTPClient;

public class Demo {

	public static void main(String[] args) {
		Server1 s1 = new Server1();
		s1.start();
		Server2 s2 = new Server2();
		s2.start();
        TestClient tc = new TestClient();
        tc.start();
		try {
			Thread.currentThread().sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Client c = new Client();
		c.start();
	}
	
	private static class Server1 extends Thread {
		public void run() {
			CameraManager.main(new String[]{"argus-5.student.lth.se", "7005", "6077"});
		}
	}
	
	private static class Server2 extends Thread {
		public void run() {
            CameraManager.main(new String[]{"argus-3.student.lth.se", "7003", "6078"});
		}
	}

    private static class TestClient extends Thread {
        public void run() {
            JPEGHTTPClient.main(new String[]{"localhost", "6077"});
        }
    }
	
	private static class Client extends Thread {
		public void run() { ClientMonitor.main(new String[]{"localhost", "6077", "localhost", "6078"});
		}
	}
}
