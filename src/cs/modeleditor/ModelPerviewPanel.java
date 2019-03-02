package cs.modeleditor;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * User: rongyang
 * Date: 4/5/12
 * Time: 7:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class ModelPerviewPanel extends JPanel {
    private JButton chooseModelBtn;

    public ModelPerviewPanel(final MerchandiseCtrlPanel ctrlPanel,
                             final ModelPropertiesPanel propertiesPanel) {
        super();
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));
        init(ctrlPanel);
        add(ctrlPanel.getDisplayer());
        ctrlPanel.getDisplayer().setPreferredSize(new Dimension(200, 200));
        ctrlPanel.getDisplayer().setBorder(BorderFactory.createTitledBorder(BorderFactory.
                createEtchedBorder(), "模型预览",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("", Font.BOLD, 13)));
        add(createImagePanel());
        add(propertiesPanel);

    }


    private void init(final ActionListener listener) {
        chooseModelBtn = new JButton("浏览模型文件");
        chooseModelBtn.addActionListener(listener);
        chooseModelBtn.setActionCommand(MerchandiseCtrlPanel.LOAD_FILE);
    }

    private JPanel createImagePanel() {
        final JPanel choosePhtoPanel = new JPanel();
        choosePhtoPanel.add(chooseModelBtn);
        return choosePhtoPanel;
    }

}
