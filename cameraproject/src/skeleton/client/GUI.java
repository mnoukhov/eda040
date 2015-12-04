package skeleton.client;

import javax.swing.*;
import java.awt.*;

class GUI extends JFrame {

    ClientMonitor c;
    JPanel imagePanelContainer;
    ImagePanel imagePanel1, imagePanel2;
    JLabel synchModeL, displayModeL;
    JButton synchB,
            asynchB,
            connectB,
            movieB,
            idleB;
    JPanel buttonPanel;
    boolean firstCall = true;

    public GUI(ClientMonitor c) {
        super();
        this.c = c;
        imagePanelContainer = new JPanel();
        imagePanel1 = new ImagePanel();
        imagePanel2 = new ImagePanel();
        imagePanelContainer.add(imagePanel1);
        imagePanelContainer.add(imagePanel2);
        this.getContentPane().setLayout(new BorderLayout());
        handleButtons();
        linkButtonListeners();
        this.getContentPane().add(imagePanelContainer, BorderLayout.NORTH);
        this.setLocationRelativeTo(null);
        this.pack();
//        this.setSize(640, 480);
        //TODO: figure this out
        this.setVisible(true);
    }

    public void refreshImage(byte[] jpeg, int panelNum) {
        getImagePanel(panelNum).refresh(jpeg);
        if (firstCall) {
            this.pack();
            this.setVisible(true);
            firstCall = false;
        }
    }

    public ImagePanel getImagePanel() {
        return imagePanel1;
    }

    public ImagePanel getImagePanel(int panelNum) {
        switch (panelNum) {
            case 1: return imagePanel1;
            case 2: return imagePanel2;
            default: return null;
        }
    }

    public void handleLabels() {

    }

    public void handleButtons() {
        synchB = new JButton("Synchronous");
        asynchB = new JButton("Asynchronous");
        connectB = new JButton("Connect");
        movieB = new JButton("Movie");
        idleB = new JButton("Idle");

        buttonPanel = new JPanel();
        buttonPanel.add(synchB);
        buttonPanel.add(asynchB);
        buttonPanel.add(connectB);
        buttonPanel.add(movieB);
        buttonPanel.add(idleB);
        this.getContentPane().add( buttonPanel, BorderLayout.SOUTH );
    }

    public void linkButtonListeners() {
        connectB.addActionListener(new ConnectButton(c));
    }
}

