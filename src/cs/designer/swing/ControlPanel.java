package cs.designer.swing;

import cs.designer.swing.icons.IconManager;
import cs.designer.swing.list.MerchandiseHtmlList;
import cs.designer.swing.tool.VerticalLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 9/5/12
 * Time: 1:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class ControlPanel extends JPanel implements ActionListener {
    public static int DEFAULT_WIDTH = 346;
    public static int DEFAULT_HEIGHT = 620;
    private JButton merchandiseUnfoldBtn;
    private JPanel merchandiseFoldPanel;
    private MerchandiseHtmlList merchandiseList;


    public ControlPanel() {
        init();
        fold(false);
        setLayout(new VerticalLayout());
    }

    private void init() {
        merchandiseFoldPanel = new JPanel();
        merchandiseFoldPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
        merchandiseList = new MerchandiseHtmlList();
        merchandiseList.setLayout(new VerticalLayout());
        merchandiseUnfoldBtn = new FoldButton(new ImageIcon(IconManager.getIconUrl("max.png")), false);
        merchandiseUnfoldBtn.setToolTipText("展开");
        merchandiseUnfoldBtn.setBorder(null);
        merchandiseUnfoldBtn.addActionListener(this);
        merchandiseFoldPanel.add(merchandiseUnfoldBtn);

    }

    private void fold(boolean foldable) {
        if (foldable) {
            remove(merchandiseList);
            add(merchandiseFoldPanel);
        } else {
            merchandiseList = new MerchandiseHtmlList();
            merchandiseList.setPreferredSize(new Dimension(DEFAULT_WIDTH,DEFAULT_HEIGHT));
            add(merchandiseList);
            remove(merchandiseFoldPanel);
        }
        updateUI();
    }

    public void setListHeight(int height) {
        setPreferredSize(new Dimension(DEFAULT_WIDTH, height));
    }


    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof FoldButton) {
            fold(((FoldButton) e.getSource()).foldable);
        }
    }


    public MerchandiseHtmlList getMerchandiseList() {
        return merchandiseList;
    }
        class FoldButton extends JButton {
        private boolean foldable = false;

        public FoldButton(final Icon icon, final boolean foldable) {
            super(icon);
            this.foldable = foldable;

        }
    }
}
