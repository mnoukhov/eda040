package skeleton.client;

import javax.swing.*;

class GUI extends JFrame {

    public static final int MODE_SYNC = 0;
    public static final int MODE_ASYNC = 1;
    public static final int MODE_IDLE = 2;
    public static final int MODE_MOVIE = 3;
    public static final int MODE_AUTO = 4;

    ClientMonitor c;
    JPanel imagePanelContainer,
           buttonPanel,
           labelPanel;
    ImagePanel imagePanel1, imagePanel2;
    JLabel synchModeL, displayModeL;
    JButton syncB,
            connectB,
            movieModeB;

    public GUI(ClientMonitor c) {
        super();
        this.c = c;

        //set up layout
        BoxLayout boxLayout = new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS);
        this.getContentPane().setLayout(boxLayout);

        //imagePanels
        setUpImagePanels();

        //buttons
        setUpButtons();

        //button listeners
        linkButtonListeners();

        //last step
        this.setLocationRelativeTo(null);
        this.pack();
        this.setVisible(true);
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

    public void setUpImagePanels() {
        imagePanelContainer = new JPanel();
        imagePanel1 = new ImagePanel();
        imagePanel2 = new ImagePanel();
        imagePanelContainer.add(imagePanel1);
        imagePanelContainer.add(imagePanel2);
        this.getContentPane().add(imagePanelContainer);
    }

    public void setUpButtons() {
        syncB = new JButton("Synchronous");
        connectB = new JButton("Connect");
        movieModeB = new JButton("Movie");

        buttonPanel = new JPanel();
        buttonPanel.add(syncB);
        buttonPanel.add(connectB);
        buttonPanel.add(movieModeB);
        this.getContentPane().add( buttonPanel );
    }

    public void setSynchModeLabel(int synchMode) {
        return;
    }

    public void linkButtonListeners() {
        connectB.addActionListener(new ConnectButton(c));
    }
}

