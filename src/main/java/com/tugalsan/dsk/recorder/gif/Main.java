package com.tugalsan.dsk.recorder.gif;

import com.tugalsan.api.desktop.server.*;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

//WHEN RUNNING IN NETBEANS, ALL DEPENDENCIES SHOULD HAVE TARGET FOLDER!
//cd C:\me\codes\com.tugalsan\dsk\com.tugalsan.dsk.recorder.gif
//java --enable-preview --add-modules jdk.incubator.concurrent -jar target/com.tugalsan.dsk.recorder.gif-1.0-SNAPSHOT-jar-with-dependencies.jar
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
