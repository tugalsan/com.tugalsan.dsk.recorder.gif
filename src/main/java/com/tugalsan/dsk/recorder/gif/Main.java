package com.tugalsan.dsk.recorder.gif;

import com.tugalsan.api.desktop.server.*;
import com.tugalsan.api.file.gif.server.TS_FileGifWriter;
import com.tugalsan.api.input.server.TS_InputScreenUtils;
import com.tugalsan.api.thread.server.killable.TS_ThreadKillableBuilder;
import com.tugalsan.api.thread.server.safe.TS_ThreadSafeLst;
import java.awt.Robot;
import java.awt.image.RenderedImage;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JFrame;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

//WHEN RUNNING IN NETBEANS, ALL DEPENDENCIES SHOULD HAVE TARGET FOLDER!
//cd C:\me\codes\com.tugalsan\dsk\com.tugalsan.dsk.recorder.gif
//java --enable-preview --add-modules jdk.incubator.concurrent -jar target/com.tugalsan.dsk.recorder.gif-1.0-SNAPSHOT-jar-with-dependencies.jar
public class Main {

    //TODO ffmpeg -f gif -i infile.gif outfile.mp4
    public static void main(String[] args) {
        TS_DesktopMainUtils.setThemeAndinvokeLaterAndFixTheme(() -> {
            var frame = new JFrame();
            frame.setDefaultCloseOperation(EXIT_ON_CLOSE);

            var resizer = TS_DesktopFrameResizer.of(frame);
            TS_DesktopWindowAndFrameUtils.initUnDecorated(frame);
            TS_DesktopWindowAndFrameUtils.setBackgroundTransparentBlack(frame);
            TS_DesktopWindowAndFrameUtils.setBorderRed(frame);
            var startTriggered = new AtomicBoolean(false);
            var stopTriggered = new AtomicBoolean(false);
            TS_DesktopWindowAndFrameUtils.setTitleSizeCenterWithMenuBar(frame, "Tuğalsan's Gif Recorder", TS_DesktopJMenuButtonBar.of(
                    TS_DesktopJMenuButton.of("Exit", mx -> {
                        if (!startTriggered.get()) {
                            System.exit(0);
                        }
                        stopTriggered.set(true);
                    }),
                    TS_DesktopJMenuButton.of("Start", ms -> {
                        ms.setVisible(false);
                        startTriggered.set(true);
                        //FETCH RECT
                        var rect = resizer.fixIt_getRectangleWithoutMenuBar();
                        TS_DesktopWindowAndFrameUtils.setUnDecoratedTransparent(frame);
                        //FETCH FILE
                        var file = TS_DesktopPathUtils.save("Save title", Optional.empty()).orElse(null);
                        if (file == null) {
                            TS_DesktopDialogInfoUtils.show("ERROR", "No file selected");
                            System.exit(0);
                        }
                        //FETCH WRITER
                        var gif = TS_FileGifWriter.open(file, 150, true);
                        //RUN
                        TS_ThreadSafeLst<RenderedImage> images = new TS_ThreadSafeLst();

                        var capture = TS_ThreadKillableBuilder.name("capture")
                                .init(() -> TS_InputScreenUtils.robot())
                                .main((killTrigger, robot) -> images.add(TS_InputScreenUtils.shotPictures((Robot) robot, rect)))
                                .fin(robot -> stopTriggered.set(true))
                                .cycle_mainValidation_mainDuration(robot -> !stopTriggered.get(), Duration.ofMillis(gif.timeBetweenFramesMS))
                                .start();

                        var write = TS_ThreadKillableBuilder.name("write").initEmpty()
                                .main((killTrigger, e) -> gif.accept(images.popFirst(img -> true)))
                                .fin(e -> stopTriggered.set(true))
                                .cycle_mainValidation(e -> !images.isEmpty())
                                .start();

                        var exit = TS_ThreadKillableBuilder.name("write").initEmpty()
                                .main((killTrigger, e) -> {
                                    if (!capture.isDead() || !write.isDead()) {
                                        return;
                                    }
                                    TS_DesktopPathUtils.run(file);
                                    System.exit(0);
                                })
                                .fin(e -> stopTriggered.set(true))
                                .cycle_mainDuration(Duration.ofSeconds(1))
                                .start();
                    })
            ));
            TS_DesktopWindowAndFrameUtils.showAlwaysInTop(frame, true);
            return frame;
        });
    }
}
