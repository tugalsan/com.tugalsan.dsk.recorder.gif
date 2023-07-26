package com.tugalsan.trm.recorder.gif;

import com.tugalsan.api.desktop.server.*;
import com.tugalsan.trm.recorder.gif.core.*;

public class Main {

    public static volatile TS_FileGifCoreGUI gui;

    //TODO ffmpeg -f gif -i infile.gif outfile.mp4
    public static void main(String[] args) {
        TS_DesktopMainUtils.setThemeAndinvokeLaterAndFixTheme(() -> gui = TS_FileGifCoreGUI.of());
    }
}
