package com.tugalsan.trm.recorder.gif.core;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

public class TS_FileGifCoreGUI extends JFrame {

    public static TS_FileGifCoreGUI of() {
        return new TS_FileGifCoreGUI();
    }

    private final JFileChooser chooser;
    private final TS_FileGifCoreGUIMouse mouse;
    private final JComponent contentPane;
    private final JMenuBar menuBar;
    private Path captureFile;
    private JMenu start, stop, exit;

    private TS_FileGifCoreGUI() {
        //FILECHOOSER
        chooser = TS_FileGifCoreGUIStatic.createFileChooser();

        //ACTIONS
        mouse = TS_FileGifCoreGUIMouse.of(this);

        //CONTENTPANE
        contentPane = (JComponent) this.getContentPane();

        //MENUBAR
        menuBar = createMenuBar();

        //FRAME
        TS_FileGifCoreGUIStatic.setupFrame(this, menuBar);
    }

    private JMenuBar createMenuBar() {
        var bar = new JMenuBar();
        exit = new JMenu("Exit");
        exit.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                if (start.isVisible()) {
                    System.exit(0);
                } else {
                    stopCapturing();
                }
            }

            @Override
            public void menuDeselected(MenuEvent e) {
            }

            @Override
            public void menuCanceled(MenuEvent e) {
            }
        });
        bar.add(exit);
        start = new JMenu("Start");
        start.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                start.setVisible(false);
                startCapturing();
            }

            @Override
            public void menuDeselected(MenuEvent e) {
            }

            @Override
            public void menuCanceled(MenuEvent e) {
            }
        });
        bar.add(start);
        return bar;
    }

    private void stopCapturing() {
        System.out.println("stopCapturing#0");
        TS_FileGifCoreConfig_CaptureThread.INSTANCE.stopCapture();
        try {
            Desktop.getDesktop().open(captureFile.toFile());
            System.exit(0);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private void startCapturing() {
        System.out.println("startCapturing#0");
        if (chooser.showSaveDialog(TS_FileGifCoreGUI.this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        captureFile = chooser.getSelectedFile().toPath();
        var captureRectangle = new Rectangle(getX() + 2, getY() + 2 + menuBar.getHeight(), getWidth() - 4, getHeight() - 4 - menuBar.getHeight());
        mouse.started = true;
        this.setBackground(new Color(0, 0, 0, 0));
        try {
            var config = TS_FileGifCoreConfig.ofARGB(captureFile, 150, true);
            TS_FileGifCoreConfig_CaptureThread.INSTANCE.runWriter(config, captureRectangle);
        } catch (Exception e1) {
            e1.printStackTrace();
            JOptionPane.showMessageDialog(TS_FileGifCoreGUI.this, "An error has occured: " + e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

}
