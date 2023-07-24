package com.tugalsan.trm.recorder.gif.core;

import java.awt.*;
import java.io.*;

public class TS_FileGifCoreConfig_CaptureThread implements Runnable {

    public static final TS_FileGifCoreConfig_CaptureThread INSTANCE = new TS_FileGifCoreConfig_CaptureThread();
    private static final Thread THREAD = new Thread(INSTANCE);
    private TS_FileGifCoreGUIConfig cfgGui;
    private TS_FileGifCoreConfig config;
    private Rectangle bounds;
    private Robot rt;

    private void createWriter(TS_FileGifCoreGUIConfig cfgGui, TS_FileGifCoreConfig config, Rectangle bounds) throws IOException, AWTException {
        this.cfgGui = cfgGui;
        this.config = config;
        this.bounds = bounds;
        rt = new Robot();
        TS_FileGifCoreConfig_WriterThread.setWriter(config);
    }

    private void startCapture() {
        if (config == null) {
            throw new IllegalStateException("Call createWriter first!");
        }
        THREAD.start();
        TS_FileGifCoreConfig_WriterThread.startWriter();
    }

    public void runWriter(TS_FileGifCoreGUIConfig cfgGui, TS_FileGifCoreConfig config, Rectangle bounds) throws IOException, AWTException {
        createWriter(cfgGui, config, bounds);
        startCapture();
    }

    public void stopCapture() {
        if (!THREAD.isAlive()) {
            throw new IllegalStateException("Call startCapture first!");
        }
        THREAD.interrupt();
        TS_FileGifCoreConfig_WriterThread.stopWriter();
    }

    @Override
    public void run() {
        while (true) {
            var time = config.timeBetweenFramesMS();
            var start = System.currentTimeMillis();
            TS_FileGifCoreConfig_WriterThread.addImage(rt.createScreenCapture(bounds));
            var stop = System.currentTimeMillis();
            if (stop - start > time) {
                if (Thread.interrupted()) {
                    break;
                }
                cfgGui.info.setText("WARNING: Can't keep up! Increase the frame delay...");
                cfgGui.info.setForeground(Color.RED);
                continue;
            }
            try {
                Thread.sleep(time - (stop - start));
            } catch (InterruptedException e) {
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
