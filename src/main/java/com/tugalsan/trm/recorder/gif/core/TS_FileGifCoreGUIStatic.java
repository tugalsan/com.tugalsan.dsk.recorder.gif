package com.tugalsan.trm.recorder.gif.core;

import java.awt.Color;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.filechooser.FileFilter;

public class TS_FileGifCoreGUIStatic {

    public static void setupFrame(JFrame frame, JMenuBar menuBar) {
        frame.setTitle("Tuğalsan's GIF Maker");
        frame.setAlwaysOnTop(true);
        frame.setUndecorated(true);
        frame.setBackground(new Color(100, 100, 100, 50));
        frame.setLocationRelativeTo(null);
        frame.setJMenuBar(menuBar);
        frame.setSize(500, 500 + menuBar.getHeight());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static JFileChooser createFileChooser() {
        var chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(false);
        chooser.setFileHidingEnabled(false);
        chooser.setDialogTitle("Save file to:");
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(new FileFilter() {
            @Override
            public String getDescription() {
                return "GIF Files";
            }

            @Override
            public boolean accept(File f) {
                return f.getName().endsWith(".gif") || f.isDirectory();
            }
        });
        return chooser;
    }

}
