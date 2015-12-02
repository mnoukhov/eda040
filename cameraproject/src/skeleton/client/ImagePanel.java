package skeleton.client;

import javax.swing.*;
import java.awt.*;
/**
 * Created by michael on 02/12/15.
 */
public class ImagePanel extends JPanel {
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
