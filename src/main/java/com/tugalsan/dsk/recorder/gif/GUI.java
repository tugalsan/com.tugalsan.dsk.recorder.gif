package com.tugalsan.dsk.recorder.gif;

import com.tugalsan.api.file.gif.server.*;
import com.tugalsan.api.desktop.server.*;
import com.tugalsan.api.unsafe.client.TGS_UnSafe;
import java.awt.Robot;
import java.awt.image.RenderedImage;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.*;
import javax.swing.event.*;

public class GUI extends JFrame {

    public static GUI of() {
        return new GUI();
    }

    private final TS_DesktopFrameResizer resizer;
    private JMenu start, exit;

    private GUI() {
        resizer = TS_DesktopFrameResizer.of(this);
        TS_DesktopWindowAndFrameUtils.initUnDecorated(this);
        TS_DesktopWindowAndFrameUtils.setBackgroundTransparentBlack(this);
        TS_DesktopWindowAndFrameUtils.setBorderRed(this);
        TS_DesktopWindowAndFrameUtils.setTitleSizeCenterWithMenuBar(this, "Tuğalsan's Gif Recorder", createMenuBar());
        TS_DesktopWindowAndFrameUtils.showAlwaysInTop(this, true);
    }

    private JMenuBar createMenuBar() {
        var bar = new JMenuBar();
        exit = new JMenu("Exit");
        exit.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                if (start.isVisible()) {
                    System.exit(0);
                } else {
                    stoppTriggered.set(true);
                }
            }

            @Override
            public void menuDeselected(MenuEvent e) {
            }

            @Override
            public void menuCanceled(MenuEvent e) {
            }
        });
        bar.add(exit);
        start = new JMenu("Start");
        start.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                start.setVisible(false);
                start();
            }

            @Override
            public void menuDeselected(MenuEvent e) {
            }

            @Override
            public void menuCanceled(MenuEvent e) {
            }
        });
        bar.add(start);
        return bar;
    }

    private void start() {
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
        //FETCH ROBOT
        var rt = TGS_UnSafe.call(() -> new Robot());
        //RUN
        ConcurrentLinkedQueue<RenderedImage> images = new ConcurrentLinkedQueue();
        var writerFinished = new AtomicBoolean(true);
        var captureFinished = new AtomicBoolean(true);
        new Thread(() -> {//CAPTURE THREAD
            while (!stoppTriggered.get()) {
                var begin = System.currentTimeMillis();
                gif.accept(rt.createScreenCapture(rect));
                var end = System.currentTimeMillis();
                TGS_UnSafe.run(() -> Thread.sleep(gif.timeBetweenFramesMS - (end - begin)));
                Thread.yield();
            }
            captureFinished.set(false);
        }).start();
        new Thread(() -> {//WRITER THREAD
            while (!stoppTriggered.get()) {
                while (!images.isEmpty()) {
                    gif.accept(images.poll());
                }
                Thread.yield();
            }
            gif.close();
            writerFinished.set(false);
        }).start();
        new Thread(() -> {//EXIT THREAD
            while (captureFinished.get() && writerFinished.get()) {
                Thread.yield();
            }
            TS_DesktopPathUtils.run(file);
            System.exit(0);
        }).start();
    }
    AtomicBoolean stoppTriggered = new AtomicBoolean(false);
}
