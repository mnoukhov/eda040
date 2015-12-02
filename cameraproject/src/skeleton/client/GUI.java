package skeleton.client;

import javax.swing.*;
import java.awt.*;

class GUI extends JFrame {

    Client c;
    ImagePanel imagePanel;
    JButton button;
    boolean firstCall = true;

    public GUI(Client c) {
        super();
        this.c = c;
        imagePanel = new ImagePanel();
        button = new JButton("Get image");
        button.addActionListener(new ButtonHandler(this));
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

