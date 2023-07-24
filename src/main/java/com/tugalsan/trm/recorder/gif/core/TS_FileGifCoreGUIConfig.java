package com.tugalsan.trm.recorder.gif.core;

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JMenu;

public class TS_FileGifCoreGUIConfig {

    private TS_FileGifCoreGUIConfig() {
        this.dimScreen = Toolkit.getDefaultToolkit().getScreenSize();
        this.info = new JMenu();
        this.stop = new JMenu();
        this.file = new JMenu();
    }
    final public Dimension dimScreen;
    final public JMenu info, stop, file;

    public static TS_FileGifCoreGUIConfig of() {
        return new TS_FileGifCoreGUIConfig();
    }
}
