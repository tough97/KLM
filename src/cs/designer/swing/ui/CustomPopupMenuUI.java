package cs.designer.swing.ui;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPopupMenuUI;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 2/11/12
 * Time: 1:17 AM
 * To change this template use File | Settings | File Templates.
 */
public class CustomPopupMenuUI extends BasicPopupMenuUI {
    public static ComponentUI createUI(JComponent c) {
        return new CustomPopupMenuUI();
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        popupMenu.setOpaque(false);
    }

    public Popup getPopup(JPopupMenu popup, int x, int y) {
        Popup pp = super.getPopup(popup, x, y);
        JPanel panel = (JPanel) popup.getParent();
        panel.setOpaque(false);
        return pp;
    }
}