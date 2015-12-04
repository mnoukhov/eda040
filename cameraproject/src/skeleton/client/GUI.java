package skeleton.client;

import se.lth.cs.realtime.PeriodicThread;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class GUI extends JFrame {

    ClientMonitor c;
    JPanel buttonPanel;
    ImagePanel imagePanel1, imagePanel2;
    JButton syncB, connectB, movieB, idleB, autoB;

    public GUI(ClientMonitor c) {
        super();
        this.c = c;

        //set up layout
        this.getContentPane().setLayout(new BorderLayout());

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
        imagePanel1 = new ImagePanel();
        imagePanel2 = new ImagePanel();
        this.getContentPane().add(imagePanel1, BorderLayout.WEST);
        this.getContentPane().add(imagePanel2, BorderLayout.EAST);
    }

    public void setUpButtons() {
        syncB = new JButton("Synchronous");
        connectB = new JButton("Connect");
        movieB = new JButton("Movie");
        idleB = new JButton("Idle");
        autoB = new JButton("Auto");

        buttonPanel = new JPanel();
        buttonPanel.add(syncB);
        buttonPanel.add(connectB);
        buttonPanel.add(movieB);
        buttonPanel.add(idleB);
        buttonPanel.add(autoB);
        this.getContentPane().add( buttonPanel, BorderLayout.SOUTH );
    }

    public void setSynchModeLabel(int synchMode) {
        return;
    }

    public void linkButtonListeners() {
        connectB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean success = c.connectButton();
                if (success) {
                    if (c.isConnected()) {
                        connectB.setText("Disconnect");
                    } else {
                        connectB.setText("Connect");
                    }
                }
            }
        });

        idleB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                c.idleButton();
            }
        });

        movieB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                c.movieButton();
            }
        });

//        autoB.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                c.autoButton();
//            }
//        });

        syncB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                c.syncButton();
            }
        });

    }
}

