package cs.modeleditor;


import cs.designer.swing.ActionCommandKey;
import cs.designer.swing.icons.IconManager;
import cs.designer.swing.ui.CoButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class TooPanel extends JPanel {
    public TooPanel(final ActionListener listener) {
        super();
        init(listener);
        setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        ToolTipManager.sharedInstance().setInitialDelay(0);
        ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);
    }

    private void init(final ActionListener actionListener) {
        add(createFilePanel(actionListener));
    }

    private JPanel createFilePanel(final ActionListener actionListener) {
        final JPanel filePanl = new JPanel();
        filePanl.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
        final JButton saveFileBtn = new CoButton(IconManager.getSelectedIcons("save"));
        saveFileBtn.setActionCommand(ActionCommandKey.SAVE_AS_FILE);
        saveFileBtn.addActionListener(actionListener);
        saveFileBtn.setToolTipText("保存模型");
        final JButton loadFileBtn = new CoButton(IconManager.getSelectedIcons("open"));
        loadFileBtn.setActionCommand(ActionCommandKey.LOAD_FILE);
        loadFileBtn.addActionListener(actionListener);
        loadFileBtn.setToolTipText("打开模型");
        final JButton newFile = new CoButton(IconManager.getSelectedIcons("newfile"));
        newFile.setActionCommand(ActionCommandKey.NEW_FILE);
        newFile.addActionListener(actionListener);
        newFile.setToolTipText("新建文件");
        filePanl.add(newFile);
        filePanl.add(loadFileBtn);
        filePanl.add(saveFileBtn);
        return filePanl;
    }
}
