package cs.designer.swing.ui;

import cs.designer.swing.resources.ResourcesPath;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 4/16/12
 * Time: 7:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class LoadingPanel extends JPanel {
    public LoadingPanel() {
        super();
        init();
        setOpaque(false);
    }

    private void init() {
        setLayout(new BorderLayout());
        final JLabel loadinglLabel = new JLabel(new ImageIcon(ResourcesPath.getResourcesUrl("loading.gif")));
        add(BorderLayout.CENTER, loadinglLabel);
    }
}
