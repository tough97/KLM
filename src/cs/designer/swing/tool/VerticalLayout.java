package cs.designer.swing.tool;

import java.awt.*;


/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 2/23/12
 * Time: 5:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class VerticalLayout implements LayoutManager{
    private int gap = 0;

    public VerticalLayout() {
    }

    public VerticalLayout(int gap) {
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
        int width = size.width - insets.left - insets.right;
        int height = insets.top;

        int i = 0;
        for (int c = parent.getComponentCount(); i < c; i++) {
            Component m = parent.getComponent(i);
            if (m.isVisible()) {
                m.setBounds(insets.left, height, width, m.getPreferredSize().height);
                height += m.getSize().height + this.gap;
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

                pref.height += componentPreferredSize.height + this.gap;
                pref.width = Math.max(pref.width, componentPreferredSize.width);
            }
        }

        pref.width += insets.left + insets.right;
        pref.height += insets.top + insets.bottom;

        return pref;
    }

    public void removeLayoutComponent(Component c) {
    }
}