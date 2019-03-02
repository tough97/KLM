package cs.designer.screen;

import cs.designer.swing.ui.LoadingPanel;
import cs.designer.view.controller.DisplayControlable;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 8/28/12
 * Time: 1:48 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class KLMScreen extends JPanel {
    private JPanel parentPanel;

    public KLMScreen(JPanel parentPanel) {
        this.parentPanel = parentPanel;
    }

    public abstract void updateScreen();

    public void display() {
        updateScreen();
        parentPanel.removeAll();
        parentPanel.setLayout(new BorderLayout());
        parentPanel.add(BorderLayout.CENTER, this);
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                parentPanel.updateUI();
            }
        });


    }

}
