package com.tugalsan.trm.recorder.gif;

import com.tugalsan.trm.recorder.gif.core.*;
import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        //ffmpeg -f gif -i infile.gif outfile.mp4

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
        SwingUtilities.invokeLater(() -> TS_FileGifCoreGUI.of());
    }
}
