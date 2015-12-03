package skeleton.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by michael on 02/12/15.
 */
public class ButtonHandler implements ActionListener {

    GUI gui;

    public ButtonHandler(GUI gui) {
        this.gui = gui;
    }

    public void actionPerformed(ActionEvent evt) {
//        gui.refreshImage();
    }
}

