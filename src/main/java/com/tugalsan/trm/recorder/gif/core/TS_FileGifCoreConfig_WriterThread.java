package com.tugalsan.trm.recorder.gif.core;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TS_FileGifCoreConfig_WriterThread implements Runnable {

    private static final ConcurrentLinkedQueue<BufferedImage> images = new ConcurrentLinkedQueue();

    private static final TS_FileGifCoreConfig_WriterThread INSTANCE = new TS_FileGifCoreConfig_WriterThread();
    private static final Thread THREAD = new Thread(INSTANCE);
    private static TS_FileGifCoreConfig config;
    private static final Object lock = new Object();

    public static void addImage(BufferedImage img) {
        images.add(img);
    }

    public static void setWriter(TS_FileGifCoreConfig config) {
        TS_FileGifCoreConfig_WriterThread.config = config;
    }

    public static void startWriter() {
        if (!THREAD.isAlive()) {
            THREAD.start();
        }
    }

    public static void stopWriter() {
        while (THREAD.isAlive()) {
            THREAD.interrupt();
            synchronized (lock) {
                try {
                    lock.wait(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            while (!images.isEmpty()) {
                var image = images.poll();
                try {
                    config.append(image);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Thread.yield();
        }
        try {
            config.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        synchronized (lock) {
            lock.notify();
        }
    }
}
