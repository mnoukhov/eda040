package cameraproject.client;

import javax.swing.*;
import java.awt.*;
import java.awt.Image;

public class ImagePanel extends JPanel {
    ImageIcon icon;
    JLabel delayLabel;

    public ImagePanel() {
        super();
        this.setLayout(new BorderLayout());
        icon = new ImageIcon();
        JLabel image = new JLabel(icon);
        delayLabel = new JLabel("Delay:");
        this.add(image, BorderLayout.CENTER);
        this.add(delayLabel, BorderLayout.SOUTH);
        this.setPreferredSize(new Dimension(640, 500));
    }

    public void refresh(byte[] data) {
        Image theImage = getToolkit().createImage(data);
        getToolkit().prepareImage(theImage,-1,-1,null);
        icon.setImage(theImage);
        icon.paintIcon(this, this.getGraphics(), 5, 5);
    }

    public void setDelayLabel(long millis) {
        delayLabel.setText("Delay: " + millis + " ms");
    }
}
