package cs.designer.swing.ui;

import cs.designer.swing.bean.LocationBean;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 3/14/12
 * Time: 12:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class CoLable extends JLabel implements MouseInputListener {
    private String text;
    private ArrayList<ActionListener> listeners = new ArrayList<ActionListener>();
    public static Color DEFAULT_FONT_COLOR = new Color(100, 100, 100);
    public static Color DEFAULT_ROLLOVER_COLOR = new Color(200, 197, 197);
    private Color fontColor = DEFAULT_FONT_COLOR;
    private Color rolloverColor = DEFAULT_ROLLOVER_COLOR;


    public CoLable() {
        addMouseListener(this);
        addMouseMotionListener(this);
        setBorder(null);
        setForeground(fontColor);
    }

    public CoLable(final String text) {
        super(text, SwingConstants.CENTER);
        this.text = text;
        addMouseListener(this);
        addMouseMotionListener(this);
        setBorder(null);
        setForeground(fontColor);
    }

    public CoLable(final String text,
                   final Color fontColor) {
        super(text);
        this.text = text;
        this.fontColor = fontColor;
        addMouseListener(this);
        addMouseMotionListener(this);
        setBorder(null);
        setForeground(fontColor);
    }


    public CoLable(final String text,
                   final Color fontColor,
                   final Color rolloverColor) {
        super(text);
        this.text = text;
        this.fontColor = fontColor;
        this.rolloverColor = rolloverColor;
        addMouseListener(this);
        addMouseMotionListener(this);
        setBorder(null);
        setForeground(fontColor);
    }

    public CoLable(final Color fontColor) {
        super();
        this.text = text;
        this.fontColor = fontColor;
        this.rolloverColor = rolloverColor;
        addMouseListener(this);
        addMouseMotionListener(this);
        setBorder(null);
        setForeground(fontColor);
    }
    public void setText(final String text) {
        this.text = text;
        super.setText(text);
    }

    public void addActionListener(ActionListener l) {
        if (!listeners.contains(l))
            listeners.add(l);
    }

    public void removeActionListener(ActionListener l) {
        if (listeners.contains(l))
            listeners.remove(l);
    }

    protected void fireActionPerformed(ActionEvent e) {
        for (ActionListener listener : listeners)
            listener.actionPerformed(e);
    }

    public void mouseClicked(MouseEvent e) {
        fireActionPerformed(new ActionEvent(this, 0, "colabel action"));
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
        setForeground(rolloverColor);
        super.setText(text);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public void mouseExited(MouseEvent e) {
        setForeground(fontColor);
        super.setText(text);
        setCursor(Cursor.getDefaultCursor());
    }

    public void mouseDragged(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
    }

    public String getText() {
        return text;
    }

    public void setFontColor(Color fontColor) {
        this.fontColor = fontColor;
    }

    public void setRolloverColor(Color rolloverColor) {
        this.rolloverColor = rolloverColor;
    }
}