package com.tugalsan.dsk.recorder.gif;

import com.tugalsan.api.desktop.server.*;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class Main {

    public static volatile GUI gui;

    //TODO ffmpeg -f gif -i infile.gif outfile.mp4
    public static void main(String[] args) {
        TS_DesktopMainUtils.setThemeAndinvokeLaterAndFixTheme(() -> {
            gui = GUI.of();
            gui.setDefaultCloseOperation(EXIT_ON_CLOSE);
            return gui;
        });
    }
}
