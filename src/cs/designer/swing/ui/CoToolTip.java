package cs.designer.swing.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 1/7/12
 * Time: 6:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class CoToolTip extends JToolTip {
    public final static int DEFAULT_WIDTH = 200;
    public final static int DEFAULT_HEIGHT = 200;
    private ColorSet selectedColorSet;
    private int width = DEFAULT_WIDTH;
    private int height = DEFAULT_HEIGHT;

    public CoToolTip() {
        super();
        this.setOpaque(false);
        selectedColorSet = new ColorSet();
        selectedColorSet.topGradColor1 = new Color(233, 237, 248);
        selectedColorSet.topGradColor2 = new Color(158, 199, 240);

        selectedColorSet.bottomGradColor1 = new Color(112, 173, 239);
        selectedColorSet.bottomGradColor2 = new Color(183, 244, 253);
    }

    public CoToolTip(int width, int height) {
        this();
        this.width = width;
        this.height = height;
    }

    public void paintComponent(Graphics g) {
        Shape round = new RoundRectangle2D.Float(0, 0,
                DEFAULT_WIDTH,
                DEFAULT_HEIGHT,
                10, 10);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setPaint(new GradientPaint(0, 0, selectedColorSet.topGradColor1, 0,
                getHeight() / 2, selectedColorSet.topGradColor2));
        g2.fill(round);
        g2.setPaint(new GradientPaint(0, getHeight() / 2, selectedColorSet.bottomGradColor1, 0,
                getHeight(), selectedColorSet.bottomGradColor2));
        g2.fill(round);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_DEFAULT);
        String text = this.getComponent().getToolTipText() == null ?
                " " : this.getComponent().getToolTipText();

        if (text != null) {
            FontMetrics fm = g2.getFontMetrics();
            int h = fm.getAscent();
            g2.setColor(Color.white);
            g2.drawString(text, 10, 50);
        }
    }


    public Dimension getPreferredSize() {
        Dimension dim = super.getPreferredSize();
        return new Dimension(width, height);
    }

    private class ColorSet {
        Color topGradColor1;
        Color topGradColor2;
        Color bottomGradColor1;
        Color bottomGradColor2;
    }
}