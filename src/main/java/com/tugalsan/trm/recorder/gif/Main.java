package com.tugalsan.trm.recorder.gif;

import com.tugalsan.api.desktop.server.*;
import com.tugalsan.trm.recorder.gif.core.*;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class Main {

    public static volatile TS_FileGifCoreGUI gui;

    //TODO ffmpeg -f gif -i infile.gif outfile.mp4
    public static void main(String[] args) {
        TS_DesktopMainUtils.setThemeAndinvokeLaterAndFixTheme(() -> {
            gui = TS_FileGifCoreGUI.of();
            gui.setDefaultCloseOperation(EXIT_ON_CLOSE);
            return gui;
        });
    }
}
