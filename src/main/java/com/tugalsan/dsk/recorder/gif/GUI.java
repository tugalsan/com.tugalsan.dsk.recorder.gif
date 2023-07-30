package com.tugalsan.dsk.recorder.gif;

import com.tugalsan.api.file.gif.server.*;
import com.tugalsan.api.desktop.server.*;
import com.tugalsan.api.input.server.TS_InputScreenUtils;
import com.tugalsan.api.thread.server.async.TS_ThreadAsync;
import com.tugalsan.api.unsafe.client.TGS_UnSafe;
import java.awt.Robot;
import java.awt.image.RenderedImage;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.*;

public class GUI extends JFrame {

    public static GUI of() {
        return new GUI();
    }

    private final TS_DesktopFrameResizer resizer;

    private GUI() {
        resizer = TS_DesktopFrameResizer.of(this);
        TS_DesktopWindowAndFrameUtils.initUnDecorated(this);
        TS_DesktopWindowAndFrameUtils.setBackgroundTransparentBlack(this);
        TS_DesktopWindowAndFrameUtils.setBorderRed(this);
        var startTriggered = new AtomicBoolean(false);
        var stopTriggered = new AtomicBoolean(false);
        TS_DesktopWindowAndFrameUtils.setTitleSizeCenterWithMenuBar(this, "Tuğalsan's Gif Recorder", TS_DesktopJMenuButtonBar.of(
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
                    TS_DesktopWindowAndFrameUtils.setUnDecoratedTransparent(this);
                    //FETCH FILE
                    var file = TS_DesktopPathUtils.save("Save title", Optional.empty()).orElse(null);
                    if (file == null) {
                        TS_DesktopDialogInfoUtils.show("ERROR", "No file selected");
                        System.exit(0);
                    }
                    //FETCH WRITER
                    var gif = TS_FileGifWriter.of(file, 150, true);
                    //RUN
                    ConcurrentLinkedQueue<RenderedImage> images = new ConcurrentLinkedQueue();
                    var captureAlive = new AtomicBoolean(true);
                    TS_ThreadAsync.now(() -> {//CAPTURE THREAD
                        var r = TS_InputScreenUtils.robot();
                        while (!stopTriggered.get()) {
                            var begin = System.currentTimeMillis();
                            images.add(TS_InputScreenUtils.shotPictures(r, rect));
                            var end = System.currentTimeMillis();
                            TGS_UnSafe.run(() -> Thread.sleep(gif.timeBetweenFramesMS - (end - begin)));
                            Thread.yield();
                        }
                        captureAlive.set(false);
                    });
                    var writerAlive = new AtomicBoolean(true);
                    TS_ThreadAsync.now(() -> {//WRITE THREAD
                        while (!stopTriggered.get()) {
                            while (!images.isEmpty()) {
                                gif.accept(images.poll());
                            }
                            Thread.yield();
                        }
                        gif.close();
                        writerAlive.set(false);
                    });
                    TS_ThreadAsync.now(() -> {//EXIT THREAD
                        while (captureAlive.get() && writerAlive.get()) {
                            Thread.yield();
                        }
                        TS_DesktopPathUtils.run(file);
                        System.exit(0);
                    });
                })
        ));
        TS_DesktopWindowAndFrameUtils.showAlwaysInTop(this, true);
    }
}
