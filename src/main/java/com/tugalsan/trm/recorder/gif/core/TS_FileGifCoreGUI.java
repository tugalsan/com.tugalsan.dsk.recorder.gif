package com.tugalsan.trm.recorder.gif.core;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Properties;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;

public class TS_FileGifCoreGUI extends JFrame implements MouseInputListener, KeyListener {

    JPanel p;
    Rectangle rectRecord;
    JMenuBar bar;
    Path saveFile;
    JFileChooser chooser;
    Properties props = new Properties();
    JButton start;
    Timer flash;

    final public TS_FileGifCoreGUIConfig cfgGui;

    public static TS_FileGifCoreGUI of() {
        return new TS_FileGifCoreGUI(TS_FileGifCoreGUIConfig.of());
    }

    public static TS_FileGifCoreGUI of(TS_FileGifCoreGUIConfig cfgGui) {
        return new TS_FileGifCoreGUI(cfgGui);
    }

    private TS_FileGifCoreGUI(TS_FileGifCoreGUIConfig cfgGui) {
        this.cfgGui = cfgGui;
        setAlwaysOnTop(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Tuğalsan's GIF Maker");
        setUndecorated(true);
        setBackground(new Color(100, 100, 100, 50));
        setLocationRelativeTo(null);
        addMouseListener(this);
        addKeyListener(this);
        addMouseMotionListener(this);
        ((JComponent) getContentPane()).setBorder(new LineBorder(Color.BLACK, 2));
        p = new JPanel(new FlowLayout());
        p.setBorder(new EmptyBorder(50, 50, 50, 50));
        start = new JButton("File > Save to...");
        start.addActionListener(evt -> {
            p.setVisible(false);
            getContentPane().setCursor(Cursor.getDefaultCursor());
            started = true;
            cfgGui.stop.setVisible(true);
            cfgGui.info.setText("< Click to stop");
            TS_FileGifCoreGUI.this.setBackground(new Color(0, 0, 0, 0));
            rectRecord = new Rectangle(getX() + 2, getY() + 2 + bar.getHeight(), getWidth() - 4, getHeight() - 4 - bar.getHeight());
            ((JComponent) TS_FileGifCoreGUI.this.getContentPane()).setBorder(new LineBorder(Color.RED, 2));
            cfgGui.file.setVisible(false);
            cfgGui.stop.setEnabled(true);
            try {
                var config = TS_FileGifCoreConfig.ofARGB(saveFile, 150, true);
                TS_FileGifCoreConfig_CaptureThread.INSTANCE.runWriter(cfgGui, config, rectRecord);
            } catch (Exception e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(TS_FileGifCoreGUI.this, "An error has occured: " + e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
            flash.start();
        });
        start.setFont(start.getFont().deriveFont(30f));
        start.setEnabled(false);
        start.setOpaque(false);
        start.setCursor(Cursor.getDefaultCursor());
        p.add(start);
        var label = new JLabel("Drag me and resize me. This will be the capture area.");
        label.setBackground(UIManager.getColor("Panel.background"));
        label.setOpaque(true);
        label.setForeground(Color.BLUE);
        label.setFont(label.getFont().deriveFont(20f));
        p.add(label);
        p.setOpaque(false);
        p.setBackground(new Color(0, 0, 0, 0));
        ((JComponent) getContentPane()).setOpaque(false);
        getContentPane().setBackground(new Color(0, 0, 0, 0));
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(p, BorderLayout.CENTER);

        try (var in = new FileInputStream("sc.prefs")) {
            props.load(in);
        } catch (Exception e) {
        }

        chooser = new JFileChooser(props.getProperty("directory", FileSystemView.getFileSystemView().getDefaultDirectory().getAbsolutePath()));
        chooser.setMultiSelectionEnabled(false);
        chooser.setFileHidingEnabled(false);
        chooser.setDialogTitle("Save file to:");
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(new FileFilter() {
            @Override
            public String getDescription() {
                return "GIF Files";
            }

            @Override
            public boolean accept(File f) {
                return f.getName().endsWith(".gif") || f.isDirectory();
            }
        });

        bar = new JMenuBar();

        var file = new JMenu("File");
        var save = new JMenuItem("Save to...");
        save.addActionListener(evt -> {
            if (chooser.showSaveDialog(TS_FileGifCoreGUI.this) == JFileChooser.APPROVE_OPTION) {
                saveFile = chooser.getSelectedFile().toPath();
                props.setProperty("directory",
                        chooser.getCurrentDirectory().getAbsolutePath());
                savePrefs();
                start.setText("Start");
                start.setEnabled(true);
            }
        });
        file.add(save);
        var exit = new JMenuItem("Exit");
        exit.addActionListener((ActionEvent e) -> {
            System.exit(0);
        });
        file.add(exit);
        bar.add(file);

        var stop = new JMenu("Stop Capture");
        stop.setEnabled(false);
        stop.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!stop.isEnabled()) {
                    return;
                }
                stop.setSelected(false);
                stop.setText("Waiting for GIF to save...");
                stop.setEnabled(false);
                flash.stop();
                cfgGui.info.setVisible(false);
                new Thread(() -> {
                    TS_FileGifCoreConfig_CaptureThread.INSTANCE.stopCapture();
                    stop.setText("Opening file...");
                    try {
                        Desktop.getDesktop().open(saveFile.toFile());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    System.exit(0);
                }).start();
            }
        });
        stop.setVisible(false);
        bar.add(stop);

        cfgGui.info.setText("File > Save to... to start");
        cfgGui.info.setEnabled(false);
        cfgGui.info.setFont(cfgGui.info.getFont().deriveFont(Font.ITALIC));
        bar.add(cfgGui.info);

        setJMenuBar(bar);
        bar.setCursor(Cursor.getDefaultCursor());

        setSize(500, 500 + bar.getHeight());
        setLocationRelativeTo(null);

        flash = new Timer(1000, new ActionListener() {
            boolean on = true;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                on = !on;
                ((JComponent) TS_FileGifCoreGUI.this.getContentPane()).setBorder(new LineBorder(on ? Color.RED : Color.BLACK, 2));
            }
        });
        flash.setCoalesce(true);
        flash.setRepeats(true);

        setVisible(true);
        System.out.println("Init done");
    }

    protected void savePrefs() {
        try {
            var out = new FileOutputStream("sc.prefs");
            System.out.println(new File("sc.prefs").getAbsolutePath());
            props.store(out, "Edit if you want, kinda useless");
            out.close();
        } catch (Exception e1) {
        }
    }

    boolean pressed = false;
    boolean started = false;
    boolean onedge = false;
    int edge;
    int iwx, iwy;
    int ix, iy;
    int iw, ih;

    @Override
    public void mouseClicked(MouseEvent arg0) {

    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
    }

    @Override
    public void mousePressed(MouseEvent arg0) {
        pressed = true;
        iwx = arg0.getX();
        iwy = arg0.getY();
        ix = arg0.getXOnScreen();
        iy = arg0.getYOnScreen();
        iw = getWidth();
        ih = getHeight();
        if (iwx < 5) {
            onedge = true;
            edge = 1;
        } else if (iwy < 5) {
            onedge = true;
            edge = 4;
        } else if (iwy > getHeight() - 5) {
            onedge = true;
            edge = 2;
        } else if (iwx > getWidth() - 5) {
            onedge = true;
            edge = 3;
        }
    }

    @Override
    public void mouseReleased(MouseEvent arg0) {
        pressed = false;
        onedge = false;
    }

    @Override
    public void mouseDragged(MouseEvent arg0) {
        initCaching();
        if (started) {
            return;
        }
        if (pressed && !onedge) {
            int x = arg0.getXOnScreen() - iwx, y = arg0.getYOnScreen() - iwy;
            setLocation(x, y, true);
        } else if (pressed && onedge) {
            switch (edge) {
                case 3 ->
                    setSize(arg0.getXOnScreen() - (iw - iwx) - getX(true), getHeight(true), true);
                case 2 ->
                    setSize(getWidth(true), arg0.getYOnScreen() - (ih - iwy) - getY(true), true);
                case 1 -> {
                    setLocation(arg0.getXOnScreen(), getY(true), true);
                    setSize(iw + ix - arg0.getXOnScreen(), getHeight(true), true);
                }
                case 4 -> {
                    setLocation(getY(true), arg0.getYOnScreen(), true);
                    setSize(getWidth(true), ih + iy - arg0.getYOnScreen(), true);
                }
                default -> {
                }
            }
        }
        if (getX(true) < 0) {
            setLocation(0, getY(true), true);
        }
        if (getY(true) < 0) {
            setLocation(getX(true), 0, true);
        }
        if (getX(true) + getWidth(true) > cfgGui.dimScreen.width) {
            setLocation(cfgGui.dimScreen.width - getWidth(true), getY(true), true);
        }
        if (getY(true) + getHeight(true) > cfgGui.dimScreen.height) {
            setLocation(getX(true), cfgGui.dimScreen.height - getHeight(true), true);
        }
        if (getWidth(true) > cfgGui.dimScreen.width) {
            setSize(cfgGui.dimScreen.width, getHeight(true), true);
        }
        if (getHeight(true) > cfgGui.dimScreen.height) {
            setSize(getWidth(true), cfgGui.dimScreen.height, true);
        }
        sendSizeChange();
        sendLocationChange();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (started) {
            return;
        }
        if (e.getX() < 5) {
            getContentPane().setCursor(
                    Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
        } else if (e.getY() < 5) {
            getContentPane().setCursor(
                    Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
        } else if (e.getY() > getHeight() - 5) {
            getContentPane().setCursor(
                    Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
        } else if (e.getX() > getWidth() - 5) {
            getContentPane().setCursor(
                    Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
        } else {
            getContentPane().setCursor(
                    Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        }
    }

    int tx, ty, tw, th;

    public void initCaching() {
        tx = getX();
        ty = getY();
        tw = getWidth();
        th = getHeight();
    }

    public void setSize(int w, int h, boolean wait) {
        tw = w;
        th = h;
    }

    public void setLocation(int x, int y, boolean wait) {
        tx = x;
        ty = y;
    }

    public int getWidth(boolean wait) {
        return tw;
    }

    public int getHeight(boolean wait) {
        return th;
    }

    public int getX(boolean wait) {
        return tx;
    }

    public int getY(boolean wait) {
        return ty;
    }

    public void sendSizeChange() {
        setSize(tw, th);
    }

    public void sendLocationChange() {
        setLocation(tx, ty);
    }

    @Override
    public void keyPressed(KeyEvent arg0) {
        if (arg0.getKeyCode() != KeyEvent.VK_S) {
            return;
        }
        Arrays.stream(cfgGui.stop.getActionListeners()).forEachOrdered(a -> a.actionPerformed(null));
    }

    @Override
    public void keyReleased(KeyEvent arg0) {
    }

    @Override
    public void keyTyped(KeyEvent arg0) {
    }
}
