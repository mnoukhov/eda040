package skeleton.client;

import javax.swing.*;
import java.awt.*;

class GUI extends JFrame {

    ClientManager c;
    ImagePanel imagePanel;
    JButton bSynch,
            bAsynch,
            bConnect,
            bMovie,
            bIdle;
    boolean firstCall = true;

    public GUI(ClientManager c) {
        super();
        this.c = c;
        imagePanel = new ImagePanel();
        bConnect = new JButton("Connect");
        bConnect.addActionListener(new ConnectButton(c));
        //button = new JButton("Get image");
        //bConnect.addActionListener(new ButtonHandler(this));
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(imagePanel, BorderLayout.NORTH);
        this.setLocationRelativeTo(null);
        this.pack();
//        this.setSize(640, 480);
        //TODO: figure this out
        this.setVisible(true);
    }

    public void refreshImage(byte[] jpeg) {
        imagePanel.refresh(jpeg);
        if (firstCall) {
            this.pack();
            this.setVisible(true);
            firstCall = false;
        }
    }

    public ImagePanel getImagePanel() {
        return imagePanel;
    }

    public void initButtons() {
        bSynch = new JButton("Synchronous");
        bAsynch = new JButton("Asynchronous");
        bConnect = new JButton("Connect");
        bMovie = new JButton("Movie");
        bIdle = new JButton("Idle");
    }

    public void addButtonsToGUI() {
        this.getContentPane().add(bSynch, BorderLayout.SOUTH);
        this.getContentPane().add(bAsynch, BorderLayout.SOUTH);
        this.getContentPane().add(bConnect, BorderLayout.SOUTH);
        this.getContentPane().add(bMovie, BorderLayout.SOUTH);
        this.getContentPane().add(bIdle, BorderLayout.SOUTH);
    }

    public void handleButtons() {

    }
}

