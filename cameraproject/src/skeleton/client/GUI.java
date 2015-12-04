package skeleton.client;

import javax.swing.*;
import java.awt.*;

class GUI extends JFrame {

    ClientManager c;
    ImagePanel imagePanel;
    JLabel synchModeL, displayModeL;
    JButton synchB,
            asynchB,
            connectB,
            movieB,
            idleB;
    JPanel buttonPanel;
    boolean firstCall = true;

    public GUI(ClientManager c) {
        super();
        this.c = c;

        imagePanel = new ImagePanel();
        this.getContentPane().setLayout(new BorderLayout());
        handleButtons();
        linkButtonListeners();
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

    public void handleLabels() {

    }
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

