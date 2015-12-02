package skeleton.client;

import javax.swing.*;
import java.awt.*;

class GUI extends JFrame {

    ImagePanel imagePanel;
    JButton button;
    boolean firstCall = true;

    public GUI() {
        super();
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
        imagePanel.refresh(jpeg);
        if (firstCall) {
            this.pack();
            this.setVisible(true);
            firstCall = false;
        }
    }
}

