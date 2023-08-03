package com.tugalsan.dsk.recorder.gif;

import com.tugalsan.api.thread.server.struct.TS_ThreadStructBuilder;
import com.tugalsan.api.desktop.server.*;
import com.tugalsan.api.file.gif.server.*;
import com.tugalsan.api.input.server.*;
import com.tugalsan.api.thread.server.safe.*;
import java.awt.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;

//WHEN RUNNING IN NETBEANS, ALL DEPENDENCIES SHOULD HAVE TARGET FOLDER!
//cd C:\me\codes\com.tugalsan\dsk\com.tugalsan.dsk.recorder.gif
//java --enable-preview --add-modules jdk.incubator.concurrent -jar target/com.tugalsan.dsk.recorder.gif-1.0-SNAPSHOT-jar-with-dependencies.jar
public class Main {

    //TODO ffmpeg -f gif -i infile.gif outfile.mp4
    public static void main(String[] args) {
        TS_DesktopMainUtils.setThemeAndinvokeLaterAndFixTheme(() -> {
            var frame = new JFrame();
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            var resizer = TS_DesktopFrameResizer.of(frame);
            TS_DesktopWindowAndFrameUtils.initUnDecorated(frame);
            TS_DesktopWindowAndFrameUtils.setBackgroundTransparentBlack(frame);
            TS_DesktopWindowAndFrameUtils.setBorderRed(frame);
            var startTriggered = TS_ThreadSafeTrigger.of();
            var killTriggered = TS_ThreadSafeTrigger.of();
            TS_DesktopWindowAndFrameUtils.setTitleSizeCenterWithMenuBar(frame, "Tuğalsan's Gif Recorder", TS_DesktopJMenuButtonBar.of(
                    TS_DesktopJMenuButton.of("Exit", mx -> {
                        if (startTriggered.hasNotTriggered()) {
                            System.exit(0);
                        }
                        killTriggered.trigger();
                    }),
                    TS_DesktopJMenuButton.of("Start", ms -> {
                        ms.setVisible(false);
                        startTriggered.trigger();
                        //FETCH RECT
                        var rect = resizer.fixIt_getRectangleWithoutMenuBar();
                        TS_DesktopWindowAndFrameUtils.setUnDecoratedTransparent(frame);
                        //FETCH FILE
                        var file = TS_DesktopPathUtils.save("Save title", Optional.empty()).orElse(null);
                        if (file == null) {
                            TS_DesktopDialogInfoUtils.show("ERROR", "No file selected");
                            System.exit(0);
                        }
                        //RUN
                        TS_ThreadSafeLst<RenderedImage> buffer = new TS_ThreadSafeLst();
                        var gifWriter = TS_FileGifWriter.open(file, 150, true);
                        TS_ThreadStructBuilder.of(killTriggered)
                                .init(() -> TS_InputScreenUtils.robot())
                                .main((killTrigger, robot) -> buffer.add(TS_InputScreenUtils.shotPictures((Robot) robot, rect)))
                                .cycle_mainDuration(gifWriter.timeBetweenFramesMS())
                                .asyncRun();
                        TS_ThreadStructBuilder.of(killTriggered)
                                .main(killTrigger -> gifWriter.write(buffer.popFirst()))
                                .fin(() -> {
                                    gifWriter.close();
                                    TS_DesktopPathUtils.run(file);
                                    System.exit(0);
                                })
                                .cycle_mainDuration(gifWriter.timeBetweenFramesMS())
                                .asyncRun();
                    })
            ));
            TS_DesktopWindowAndFrameUtils.showAlwaysInTop(frame, true);
            return frame;
        });
    }
}
