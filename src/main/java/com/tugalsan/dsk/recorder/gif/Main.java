package com.tugalsan.dsk.recorder.gif;

import com.tugalsan.api.charset.client.TGS_CharSetCast;
import com.tugalsan.api.thread.server.async.builder.TS_ThreadAsyncBuilder;
import com.tugalsan.api.desktop.server.*;
import com.tugalsan.api.file.gif.server.*;
import com.tugalsan.api.file.server.TS_FileUtils;
import com.tugalsan.api.function.client.maythrowexceptions.unchecked.TGS_FuncMTUEffectivelyFinal;
import com.tugalsan.api.input.server.*;
import com.tugalsan.api.log.server.TS_Log;
import com.tugalsan.api.thread.server.sync.*;
import java.awt.*;
import java.awt.image.*;
import java.nio.file.Path;
import java.util.*;
import javax.swing.*;

//WHEN RUNNING IN NETBEANS, ALL DEPENDENCIES SHOULD HAVE TARGET FOLDER!
//cd C:\me\codes\com.tugalsan\dsk\com.tugalsan.dsk.recorder.gif
//java --enable-preview --add-modules jdk.incubator.vector -jar target/com.tugalsan.dsk.recorder.gif-1.0-SNAPSHOT-jar-with-dependencies.jar
public class Main {

    final private static TS_Log d = TS_Log.of(Main.class);

    //TODO ffmpeg -f gif -i infile.gif outfile.mp4
    public static void main(String[] args) {
        TS_DesktopMainUtils.setThemeAndinvokeLaterAndFixTheme(() -> {
            var frame = new JFrame();
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            var resizer = TS_DesktopFrameResizer.of(frame);
            TS_DesktopWindowAndFrameUtils.initUnDecorated(frame);
            TS_DesktopWindowAndFrameUtils.setBackgroundTransparentBlack(frame);
            TS_DesktopWindowAndFrameUtils.setBorderRed(frame);
            var startTriggered = TS_ThreadSyncTrigger.of("startTriggered");
            var killTriggered = TS_ThreadSyncTrigger.of("killTriggered");
            TS_DesktopWindowAndFrameUtils.setTitleSizeCenterWithMenuBar(frame, "Tuğalsan's Gif Recorder", TS_DesktopJMenuButtonBar.of(TS_DesktopJMenuButton.of("Exit", mx -> {
                if (startTriggered.hasNotTriggered()) {
                    System.exit(0);
                }
                killTriggered.trigger("on exit");
            }),
                    TS_DesktopJMenuButton.of("Start", ms -> {
                        ms.setVisible(false);
                        startTriggered.trigger("on start");
                        //FETCH RECT
                        var rect = resizer.fixIt_getRectangleWithoutMenuBar();
                        TS_DesktopWindowAndFrameUtils.setUnDecoratedTransparent(frame);
                        //FETCH FILE
                        var file = TGS_FuncMTUEffectivelyFinal.of(Path.class).coronateAs(__ -> {
                            var _file = TS_DesktopPathUtils.save("Save title", Optional.empty()).orElse(null);
                            if (_file == null) {
                                TS_DesktopDialogInfoUtils.show("ERROR", "No file selected");
                                System.exit(0);
                            }
                            var _fileType = TS_FileUtils.getNameType(_file);
                            if (!TGS_CharSetCast.english().endsWithIgnoreCase(_fileType, ".gif")) {
                                _file = Path.of(_file.toAbsolutePath().toString() + ".gif");
                            }
                            return _file;
                        });

                        //RUN
                        TS_ThreadSyncLst<RenderedImage> buffer = TS_ThreadSyncLst.ofSlowRead();
                        var gifWriter = TS_FileGifWriter.open(file, 150, true);
                        TS_ThreadAsyncBuilder.<Robot>of(killTriggered.newChild(d.className()).newChild("shotPicture"))
                                .name("shotPicture")
                                .init(TS_InputCommonUtils::robot)
                                .main((killTrigger, robot) -> buffer.add(TS_InputScreenUtils.shotPicture(robot, rect)))
                                .cycle_mainPeriod(gifWriter.timeBetweenFramesMS())
                                .asyncRun();
                        TS_ThreadAsyncBuilder.<Void>of(killTriggered.newChild(d.className()).newChild("removeAndPopFirst"))
                                .name("removeAndPopFirst")
                                .main((killTrigger, o) -> gifWriter.write(buffer.removeAndPopFirst()))
                                .fin(() -> {
                                    gifWriter.close();
                                    TS_DesktopPathUtils.run(file);
                                    System.exit(0);
                                })
                                .cycle_mainPeriod(gifWriter.timeBetweenFramesMS())
                                .asyncRun();
                    })
            ));
            TS_DesktopWindowAndFrameUtils.showAlwaysInTop(frame, true);
            return frame;
        });
    }
}
