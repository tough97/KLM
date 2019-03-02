package cs.designer.swing.ui;

import cs.designer.swing.ActionCommandKey;
import cs.designer.swing.ControlPanel;
import cs.designer.swing.icons.IconManager;
import cs.designer.swing.resources.ResourcesPath;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 9/24/12
 * Time: 10:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class SaveHouseDialog extends JDialog {

    public SaveHouseDialog(final Window parent,
                         final ActionListener listener ) {
        super(parent, "");
        setSize(new Dimension(250, 150));
        int locationX = parent.getLocation().x + (parent.getWidth()
                - getWidth()) / 2;
        int locationY = parent.getLocation().y + (parent.getHeight()
                - getHeight()) / 2;

        setLocation(locationX, locationY);
        init(listener);
        setModal(true);
        setResizable(false);
    }

    private void init(final ActionListener listener ) {
        final JPanel backgroudPanel = new JPanel();
        backgroudPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        final JPanel rootPanel = new JPanel();
        rootPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 0));
        final JButton findPlanViewBtn = new JButton(new ImageIcon(IconManager.getIconUrl("local.png")));
        findPlanViewBtn.setToolTipText("保存到我的电脑");
        final JButton importBtn = new JButton(new ImageIcon(IconManager.getIconUrl("uploads.png")));
        importBtn.setToolTipText("保存到我的作品");
        findPlanViewBtn.addActionListener(listener);
        importBtn.addActionListener(listener);
        importBtn.setActionCommand(ActionCommandKey.UPLOAD_FILE);
        findPlanViewBtn.setActionCommand(ActionCommandKey.SAVE_AS_FILE);
        rootPanel.add(findPlanViewBtn);
        rootPanel.add(importBtn);

        backgroudPanel.add(rootPanel);
        rootPanel.setOpaque(false);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(BorderLayout.CENTER, backgroudPanel);
    }

//    private JPanel createBackgroundPanel(final JButton button,
//                                         final String title) {
//        final JPanel backGroundPanel = new JPanel();
//        backGroundPanel.setOpaque(false);
//        button.setBorder(null);
////        button.setContentAreaFilled(false);
//        backGroundPanel.setLayout(new BorderLayout());
//        final CoLable titleLabel = new CoLable(title);
//        titleLabel.setBorder(null);
//        titleLabel.setOpaque(false);
//        backGroundPanel.setLayout(new VerticalLayout());
//        backGroundPanel.add(BorderLayout.CENTER, button);
//        backGroundPanel.add(BorderLayout.SOUTH, titleLabel);
//        return backGroundPanel;
//    }


}
