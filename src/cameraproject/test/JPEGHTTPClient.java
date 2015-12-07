package cameraproject.test;

import java.io.*;
import java.net.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import se.lth.cs.eda040.fakecamera.*; // To gain access to maximum image size
import cameraproject.camera.Constants;


public class JPEGHTTPClient {

	public static void main(String[] args) {
		if (args.length!=2) {
			System.out.println("Syntax: JPEGHTTPClient <address> <port>");
			System.exit(1);
		}
		new GUI(args[0],Integer.parseInt(args[1]));
	}

}

class ImagePanel extends JPanel {
	ImageIcon icon;

	public ImagePanel() {
		super();
		icon = new ImageIcon();
		JLabel label = new JLabel(icon);
		add(label, BorderLayout.CENTER);
		this.setSize(200, 200);
	}

	public void refresh(byte[] data) {
		Image theImage = getToolkit().createImage(data);
		getToolkit().prepareImage(theImage,-1,-1,null);	    
		icon.setImage(theImage);
		icon.paintIcon(this, this.getGraphics(), 5, 5);
	}
}

class ButtonHandler implements ActionListener {

	GUI gui;

	public ButtonHandler(GUI gui) {
		this.gui = gui;
	}

	public void actionPerformed(ActionEvent evt) {
		gui.refreshImage();
	}
}

class GUI extends JFrame {

	ImagePanel imagePanel;
	JButton button;
	boolean firstCall = true;
	String server;
	int port;
	byte [] jpeg = new byte[AxisM3006V.IMAGE_BUFFER_SIZE];
	byte [] timestamp = new byte[8];

	public GUI(String server,int port) {
		super();
		this.server = server;
		this.port = port;
		imagePanel = new ImagePanel();
		button = new JButton("Get image");
		button.addActionListener(new ButtonHandler(this));
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(imagePanel, BorderLayout.NORTH);
		this.getContentPane().add(button, BorderLayout.SOUTH);
		this.setLocationRelativeTo(null);
		this.pack();
		refreshImage();
	}

	public void refreshImage() {
		try {
			// Open a socket to the server, get the input/output streams
			Socket sock = new Socket(server, port);
			InputStream is = sock.getInputStream();
			OutputStream os = sock.getOutputStream();

			// Send a simple request, always for "/image.jpg"
//			putLine(os, "GET /image.jpg HTTP/1.0");
//			putLine(os, "");        // The request ends with an empty line

			// Read the first line of the response (status line)
			String responseLine;
			responseLine = getLine(is);
			System.out.println("HTTP server says '" + responseLine + "'.");
			// Ignore the following header lines up to the final empty one.
			do {
				responseLine = getLine(is);
			} while (!(responseLine.equals("")));

            getLine(is);    //CMD_JPEG, we can ignore
            int len = Integer.parseInt(getLine(is));
            System.out.println("Reading image size " + len);
            getInputBytes(is, jpeg, len);
            getInputBytes(is, timestamp, 8);

            try {
                sendDisconnect(os);
            } catch (IOException e) {
                e.printStackTrace();
            }
			sock.close();
		}
		catch (IOException e) {
			System.out.println("Error when receiving image.");
			return;
		}


		imagePanel.refresh(jpeg);
		if (firstCall) {
			this.pack();
			this.setVisible(true);
			firstCall = false;
		}

	}
	// -------------------------------------------------------- PRIVATE METHODS

    private void sendDisconnect(OutputStream os) throws IOException {
        putLine(os, "POST mode-change HTTP/1.0");
        putLine(os, "Content-Type: text");
        putLine(os, "");                   // Means 'end of header'
        putLine(os, Constants.CMD_DISCONNECT);
    }

	private static final byte[] CRLF      = { 13, 10 };

	/**
	 * Read a line from InputStream 's', terminated by CRLF. The CRLF is
	 * not included in the returned string.
	 */
	private static String getLine(InputStream s) throws IOException {
		boolean done = false;
		String result = "";

		while(!done) {
			int ch = s.read();        // Read
			if (ch <= 0 || ch == 10) {
				// Something < 0 means end of data (closed socket)
				// ASCII 10 (line feed) means end of line
				done = true;
			}
			else if (ch >= ' ') {
				result += (char)ch;
			}
		}

		return result;
	}

	/**
	 * Send a line on OutputStream 's', terminated by CRLF. The CRLF should not
	 * be included in the string str.
	 */
	private static void putLine(OutputStream s, String str) throws IOException {
		s.write(str.getBytes());
		s.write(CRLF);
	}

    private static void getInputBytes(InputStream is, byte[] data, int n) {
        try {
            int bytesRead = 0;
            int bytesLeft = n;
            int status;

            // We have to keep reading until -1 (meaning "end of file") is
            // returned. The socket (which the stream is connected to)
            // does not wait until all data is available; instead it
            // returns if nothing arrived for some (short) time.
            do {
                status = is.read(data, bytesRead, bytesLeft);
                // The 'status' variable now holds the no. of bytes read,
                // or -1 if no more data is available
                if (status > 0) {
                    bytesRead += status;
                    bytesLeft -= status;
                }
            } while (bytesLeft > 0);

//            System.out.println("Received data (" + bytesRead + " bytes).");

        } catch (IOException e) {
            System.out.println("Error when receiving bytes.");
        }
    }
}
