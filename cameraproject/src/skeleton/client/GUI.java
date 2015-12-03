package skeleton.client;

import javax.swing.*;
import java.awt.*;

class GUI extends JFrame {

    ClientManager c;
    ImagePanel imagePanel;
    JButton button;
    boolean firstCall = true;

    public GUI(ClientManager c) {
        super();
        this.c = c;
        imagePanel = new ImagePanel();
        button = new JButton("Connect");
        button.addActionListener(new ConnectButton(c));
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(imagePanel, BorderLayout.NORTH);
        this.getContentPane().add(button, BorderLayout.SOUTH);
        this.setLocationRelativeTo(null);
        this.pack();
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
}

