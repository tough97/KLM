package cs.designer.screen.impi;


import cs.designer.screen.KLMScreen;
import cs.designer.swing.ControlPanel;
import cs.designer.swing.list.HtmlListDropTargetListener;
import cs.designer.swing.ui.CanvasPanel;

import javax.swing.*;
import java.awt.*;


/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 8/28/12
 * Time: 2:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class DesignerScreen extends KLMScreen {
    private ControlPanel controlPanel;
    private CanvasPanel canvasPanel;

    public DesignerScreen(JPanel parentPanel) {
        super(parentPanel);
        setLayout(new BorderLayout());
        canvasPanel =
                new CanvasPanel();
        canvasPanel.getPlanview().
                addDropTargetListener(
                        new HtmlListDropTargetListener(canvasPanel.getPlanview(), this));
        add(BorderLayout.CENTER, canvasPanel);
    }

    @Override
    public void updateScreen() {
        if (controlPanel != null) {
            remove(controlPanel);
        }
        controlPanel = new ControlPanel();
        add(BorderLayout.WEST, controlPanel);
    }

    public ControlPanel getControlPanel() {
        return controlPanel;
    }

    public CanvasPanel getCanvasPanel() {
        return canvasPanel;
    }
}
