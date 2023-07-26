package com.tugalsan.trm.recorder.gif.core;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.event.MouseInputListener;

public class TS_FileGifCoreGUIMouse implements MouseInputListener {

    private TS_FileGifCoreGUIMouse(JFrame frame) {
        this.frame = frame;
        this.contentPane = (JComponent) frame.getContentPane();
        frame.addMouseListener(this);
        frame.addMouseMotionListener(this);
    }
    final private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    final private JFrame frame;
    final private JComponent contentPane;
    private boolean pressed = false;
    public boolean started = false;
    private boolean onedge = false;
    private int edge;
    private int iwx, iwy;
    private int ix, iy;
    private int iw, ih;

    int tx, ty, tw, th;

    public void initCaching() {
        tx = frame.getX();
        ty = frame.getY();
        tw = frame.getWidth();
        th = frame.getHeight();
    }

    public static TS_FileGifCoreGUIMouse of(JFrame frame) {
        return new TS_FileGifCoreGUIMouse(frame);
    }

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
        iw = frame.getWidth();
        ih = frame.getHeight();
        if (iwx < 5) {
            onedge = true;
            edge = 1;
        } else if (iwy < 5) {
            onedge = true;
            edge = 4;
        } else if (iwy > frame.getHeight() - 5) {
            onedge = true;
            edge = 2;
        } else if (iwx > frame.getWidth() - 5) {
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
        if (getX(true) + getWidth(true) > screenSize.width) {
            setLocation(screenSize.width - getWidth(true), getY(true), true);
        }
        if (getY(true) + getHeight(true) > screenSize.height) {
            setLocation(getX(true), screenSize.height - getHeight(true), true);
        }
        if (getWidth(true) > screenSize.width) {
            setSize(screenSize.width, getHeight(true), true);
        }
        if (getHeight(true) > screenSize.height) {
            setSize(getWidth(true), screenSize.height, true);
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
            contentPane.setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
        } else if (e.getY() < 5) {
            contentPane.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
        } else if (e.getY() > frame.getHeight() - 5) {
            contentPane.setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
        } else if (e.getX() > frame.getWidth() - 5) {
            contentPane.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
        } else {
            contentPane.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        }
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
        frame.setSize(tw, th);
    }

    public void sendLocationChange() {
        frame.setLocation(tx, ty);
    }
}
