package cs.designer.swing.tool;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 2/23/12
 * Time: 5:20 PM
 * To change this template use File | Settings | File Templates.
 */

public class HorizontalLayout
        implements LayoutManager {
    private int gap = 0;

    public HorizontalLayout() {
    }

    public HorizontalLayout(int gap) {
        this.gap = gap;
    }

    public int getGap() {
        return this.gap;
    }

    public void setGap(int gap) {
        this.gap = gap;
    }

    public void addLayoutComponent(String name, Component c) {
    }

    public void layoutContainer(Container parent) {
        Insets insets = parent.getInsets();
        Dimension size = parent.getSize();
        int height = size.height - insets.top - insets.bottom;
        int width = insets.left;
        int i = 0;
        for (int c = parent.getComponentCount(); i < c; i++) {
            Component m = parent.getComponent(i);
            if (m.isVisible()) {
                m.setBounds(width, insets.top, m.getPreferredSize().width, height);

                width += m.getSize().width + this.gap;
            }
        }
    }

    public Dimension minimumLayoutSize(Container parent) {
        return preferredLayoutSize(parent);
    }

    public Dimension preferredLayoutSize(Container parent) {
        Insets insets = parent.getInsets();
        Dimension pref = new Dimension(0, 0);
        int i = 0;
        for (int c = parent.getComponentCount(); i < c; i++) {
            Component m = parent.getComponent(i);
            if (m.isVisible()) {
                Dimension componentPreferredSize = parent.getComponent(i).getPreferredSize();

                pref.height = Math.max(pref.height, componentPreferredSize.height);
                pref.width += componentPreferredSize.width + this.gap;
            }
        }
        pref.width += insets.left + insets.right;
        pref.height += insets.top + insets.bottom;
        return pref;
    }

    public void removeLayoutComponent(Component c) {
    }
}